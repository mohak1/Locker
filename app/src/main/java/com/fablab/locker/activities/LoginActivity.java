package com.fablab.locker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fablab.locker.Helper;
import com.fablab.locker.R;
import com.fablab.locker.SharedPref;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText regNoEditText, passwordEditText;
    private Button loginButton, signupButton;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPref.init(getApplicationContext());
        Helper.firebaseInit();

        dialog = new ProgressDialog(this); // this = YourActivity
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Logging in");
        dialog.setMessage("Please wait while we log you in");
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(false);

        checkIsLoggedIn();

        regNoEditText = findViewById(R.id.regno_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        loginButton = findViewById(R.id.login_button);
        signupButton = findViewById(R.id.signup_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String regNo = regNoEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                login(regNo, password);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                showToast("Create Account to continue");
                startActivity(intent);
                finish();
            }
        });

    }

    private void login(final String regNo, final String password) {
        dialog.show();
        Helper.mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DataSnapshot Data: ", dataSnapshot.toString());
                boolean contains = false;
                for(DataSnapshot ds : dataSnapshot.getChildren() ) {
                    if((ds.getKey()).equals(regNo)) {
                        contains = true;
                        Log.d("Username: ", regNo + password);
                        Log.d("Snap", dataSnapshot.child(regNo).child("password").getValue().toString());
                        if(dataSnapshot.child(regNo).child("password").getValue().equals(password)) {
                            SharedPref.write(SharedPref.NAME, dataSnapshot.child(regNo).child("name").getValue().toString());
                            SharedPref.write(SharedPref.REGNO, regNo);
                            SharedPref.write(SharedPref.PASSWORD, password);
                            SharedPref.write(SharedPref.EMAIL, dataSnapshot.child(regNo).child("email").getValue().toString());
                            SharedPref.write(SharedPref.PHONE, dataSnapshot.child(regNo).child("phone").getValue().toString());
                            try {
                                String lockerId = dataSnapshot.child(regNo).child("locker").getValue().toString();
                                SharedPref.write(SharedPref.LOCKER, lockerId);
                                saveQrCode(lockerId);
                            } catch (Exception e) {
                                SharedPref.write(SharedPref.LOCKER, "");
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Wrong Password");
                        }
                        dialog.dismiss();
                    }
                }
                if(!contains) {
                    showToast("Account does not exist");
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    private void saveQrCode(final String lockerId) {
        Helper.mLockerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    //Only if QR is not null
                    String qr = dataSnapshot.child(lockerId).child("qr").getValue().toString();
                    SharedPref.write(SharedPref.QR, qr);
                } catch (Exception e) {
                    //Do nothing
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                showToast("Something went wrong, please try again later.");
            }
        });
    }

    private void checkIsLoggedIn() {
        String regNo = SharedPref.read(SharedPref.REGNO, null);
        String password = SharedPref.read(SharedPref.PASSWORD, null);
        if (!(regNo == null || regNo.equals("") || password == null || password.equals(""))) {
            login(regNo, password);
        }
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
