package com.fablab.locker.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.fablab.locker.Helper;
import com.fablab.locker.R;
import com.fablab.locker.SharedPref;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    Button getALockerButton, lockButton, openButton;
    TextView lockerStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPref.init(getApplicationContext());
        Helper.firebaseInit();

        lockerStatusTextView = findViewById(R.id.lockerstatus_textview);
        getALockerButton = findViewById(R.id.getalocker_button);
        lockButton = findViewById(R.id.locklocker_button);
        openButton = findViewById(R.id.openlocker_button);

        if(SharedPref.read(SharedPref.LOCKER, null) == null || SharedPref.read(SharedPref.LOCKER, null).equals("")) {
            setLockerNotAvailableLayout();
        } else {
            setLockerAvailableLayout();
        }

        getALockerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getALocker();
            }
        });

        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lockLocker();
            }
        });

        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unlockLocker();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getALocker() {
        checkLockerAvailable();
    }

    private void lockLocker() {

    }

    private void unlockLocker() {
        Intent intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
        startActivity(intent);
    }

    private void checkLockerAvailable() {
        Helper.mLockerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isLockerAvailable = false;
                String lockerId = "";
                Log.d("Locker DataSnapshot: ", dataSnapshot.toString());
                for(DataSnapshot ds : dataSnapshot.getChildren() ) {
                    String assignedTo = ds.child("assigned_to").getValue().toString();
                    if(assignedTo.equals("")) {
                        lockerId = ds.getKey();
                        isLockerAvailable = true;
                        Log.d("Assigned to: ", ds.child("assigned_to").getValue().toString());
                        break;
                    }
                }
                if(isLockerAvailable) {
                    showLockerAvailableDialog(lockerId);
                } else {
                    showLockerNotAvailableDialog();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                showToast("Something went wrong, please try again later.");
            }
        });
    }

    private void showLockerAvailableDialog(final String lockerId) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Do you want to book locker " + lockerId + "?")
                .setMessage("Continuing will book a locker with your registration number.")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        bookALocker(lockerId);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast("Booking Cancelled");
                    }
                })
                .show();
    }

    private void showLockerNotAvailableDialog() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Locker not available!")
                .setMessage("Sorry, all the lockers are currently booked by other users.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Do nothing
                    }
                })
                .show();
    }

    private void bookALocker(String lockerId) {
        String regNo = SharedPref.read(SharedPref.REGNO, null);
        Helper.mLockerRef.child(lockerId).child("assigned_to").setValue(regNo);
        Helper.mUserRef.child(regNo).child("locker").setValue(lockerId);
        SharedPref.write(SharedPref.LOCKER, lockerId);
        setLockerAvailableLayout();
        showToast("Locker " + lockerId + " Booked!");
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    private void setLockerAvailableLayout() {
        getALockerButton.setVisibility(View.INVISIBLE);
        lockButton.setVisibility(View.VISIBLE);
        openButton.setVisibility(View.VISIBLE);
        lockerStatusTextView.setText("You are assigned locker number " + SharedPref.read(SharedPref.LOCKER, ""));
    }

    private void setLockerNotAvailableLayout() {
        getALockerButton.setVisibility(View.VISIBLE);
        lockButton.setVisibility(View.INVISIBLE);
        openButton.setVisibility(View.INVISIBLE);
        lockerStatusTextView.setText("You don't have a locker");
    }

    private void logout() {
        SharedPref.write(SharedPref.REGNO, "");
        SharedPref.write(SharedPref.NAME, "");
        SharedPref.write(SharedPref.EMAIL, "");
        SharedPref.write(SharedPref.PHONE, "");
        SharedPref.write(SharedPref.LOCKER, "");
        SharedPref.write(SharedPref.QR, "");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}