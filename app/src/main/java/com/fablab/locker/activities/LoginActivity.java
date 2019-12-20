package com.fablab.locker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fablab.locker.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Firebase mRootRef, mUserRef, mLockerRef;

    private static final String ROOT_URL = "https://digilocker-dc8c1.firebaseio.com/";
    private static final String LOCKER_URL = "https://digilocker-dc8c1.firebaseio.com/Locker";
    private static final String  USER_URL = "https://digilocker-dc8c1.firebaseio.com/User";

    private EditText regNoEditText, passwordEditText;
    private Button loginButton, signupButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mRootRef = new Firebase(ROOT_URL);
        mUserRef = new Firebase(USER_URL);
        mLockerRef = new Firebase(LOCKER_URL);

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
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            showToast("Wrong Password");
                        }
                    }
                }
                if(!contains) {
                    showToast("Account does not exist");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
