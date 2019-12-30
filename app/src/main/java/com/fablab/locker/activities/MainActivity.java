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

    Button getALockerButton, toggleButton, invalidateButton;
    TextView lockerStatusTextView, lockStatusTextView;
    boolean isLocked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPref.init(getApplicationContext());
        Helper.firebaseInit();

        lockerStatusTextView = findViewById(R.id.lockerstatus_textview);
        lockStatusTextView = findViewById(R.id.lockstatus_textview);
        getALockerButton = findViewById(R.id.getalocker_button);
        toggleButton = findViewById(R.id.togglelocker_button);
        invalidateButton = findViewById(R.id.invalidatelocker_button);

        if (SharedPref.read(SharedPref.LOCKER, null) == null || SharedPref.read(SharedPref.LOCKER, null).equals("")) {
            setLockerNotAvailableLayout();
            isLocked = true;
        } else {
            setLockerAvailableLayout();
            isLocked(SharedPref.read(SharedPref.LOCKER, null));
        }

        getALockerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getALocker();
            }
        });

        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isLocked) {
                    unlockLocker();
                } else {
                    String lockerId = SharedPref.read(SharedPref.LOCKER, null);
                    confirmLockLocker(lockerId);
                }
            }
        });

        invalidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmInvalidateLocker();
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
        if (id == R.id.menu_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getALocker() {
        checkLockerAvailable();
    }

    private void lockLocker() {
        String lockerId = SharedPref.read(SharedPref.LOCKER, null);
        try {
            Helper.mLockerRef.child(lockerId).child("locked").setValue("1");
        } catch (Exception e) {
            showToast("Something went wrong, please try again later");
            Log.e("MainActivityLockLocker", e.getMessage());
        }
        showToast("Locker locked successfully");
        setLockedLayout();
        isLocked = true;
    }

    private void unlockLocker() {
        Intent intent = new Intent(MainActivity.this, ScanQRCodeActivity.class);
        startActivity(intent);
        finish();
    }

    private void invalidateLocker() {
        String regNo = SharedPref.read(SharedPref.REGNO, null);
        String lockerId = SharedPref.read(SharedPref.LOCKER, null);
        Helper.mLockerRef.child(lockerId).child("assigned_to").setValue("");
        Helper.mLockerRef.child(lockerId).child("locked").setValue("0");
        Helper.mUserRef.child(regNo).child("locker").setValue("");
        SharedPref.write(SharedPref.LOCKER, "");
        SharedPref.write(SharedPref.QR, "");
        setLockerNotAvailableLayout();
        showToast("Locker Invalidated");
    }

    private void checkLockerAvailable() {
        Helper.mLockerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean isLockerAvailable = false;
                String lockerId = "";
                Log.d("Locker DataSnapshot: ", dataSnapshot.toString());
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    String assignedTo = ds.child("assigned_to").getValue().toString();
                    if (assignedTo.equals("")) {
                        lockerId = ds.getKey();
                        isLockerAvailable = true;
                        Log.d("Assigned to: ", ds.child("assigned_to").getValue().toString());

                        break;
                    }
                }
                if (isLockerAvailable) {
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
        Helper.mLockerRef.child(lockerId).child("locked").setValue("1");
        Helper.mUserRef.child(regNo).child("locker").setValue(lockerId);
        saveQrCode(lockerId);
//        String qr = Helper.mLockerRef.child(lockerId).child("qr").toString();
        SharedPref.write(SharedPref.LOCKER, lockerId);
        setLockerAvailableLayout();
        setLockedLayout();
        showToast("Locker " + lockerId + " Booked!");
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }

    private void setLockerAvailableLayout() {
        getALockerButton.setVisibility(View.GONE);
        toggleButton.setVisibility(View.VISIBLE);
        invalidateButton.setVisibility(View.VISIBLE);
        lockStatusTextView.setVisibility(View.VISIBLE);
        lockerStatusTextView.setText("You are assigned locker number " + SharedPref.read(SharedPref.LOCKER, ""));
    }

    private void setLockerNotAvailableLayout() {
        getALockerButton.setVisibility(View.VISIBLE);
        toggleButton.setVisibility(View.GONE);
        invalidateButton.setVisibility(View.GONE);
        lockStatusTextView.setVisibility(View.GONE);
        lockerStatusTextView.setText("You don't have a locker");

    }

    private void setLockedLayout() {
        toggleButton.setText("Unlock Locker");
        lockStatusTextView.setText("Your locker is locked");
    }

    private void setUnlockedLayout() {
        toggleButton.setText("Lock Locker");
        lockStatusTextView.setText("Your locker is unlocked");
    }

    private void saveQrCode(final String lockerId) {
        Helper.mLockerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String qr = dataSnapshot.child(lockerId).child("qr").getValue().toString();
                SharedPref.write(SharedPref.QR, qr);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                showToast("Something went wrong, please try again later.");
            }
        });
    }

    private void isLocked(final String lockerId) {
        Helper.mLockerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("Locker DataSnapshot: ", dataSnapshot.toString());
                boolean contains = false;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    if ((ds.getKey()).equals(lockerId)) {
                        contains = true;
                        if (dataSnapshot.child(lockerId).child("locked").getValue().equals("0")) {
                            isLocked = false;
                            setUnlockedLayout();
                        } else if (dataSnapshot.child(lockerId).child("locked").getValue().equals("1")) {
                            isLocked = true;
                            setLockedLayout();
                        }
                        break;
                    }
                }
                if (!contains) {
                    isLocked = true;
                    showToast("Locker does not exist, please contact the admin");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void confirmLockLocker(final String lockerId) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Do you want to Lock " + lockerId + "?")
                .setMessage("Continuing will lock your locker!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        lockLocker();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast("Cancelled");
                    }
                })
                .show();
    }

    private void confirmInvalidateLocker() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Do you want to invalidate your locker?")
                .setMessage("Continuing will invalidate your locker!")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        invalidateLocker();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showToast("Cancelled");
                    }
                })
                .show();
    }

    private void logout() {
        SharedPref.write(SharedPref.REGNO, "");
        SharedPref.write(SharedPref.PASSWORD, "");
        SharedPref.write(SharedPref.NAME, "");
        SharedPref.write(SharedPref.EMAIL, "");
        SharedPref.write(SharedPref.PHONE, "");
        SharedPref.write(SharedPref.LOCKER, "");
        SharedPref.write(SharedPref.QR, "");

        showToast("Successfully logged out");

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}