package com.fablab.locker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fablab.locker.Helper;
import com.fablab.locker.R;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {



    private EditText nameEditText, regNoEditText, emailEditText,
            passwordEditText, confirmPasswordEditText, phoneNoEditText;
    private Button signUpButton, loginButton;

    String name, regNo, email, password, confirmPassword, phoneNo = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Helper.firebaseInit();

        nameEditText = findViewById(R.id.name_edittext);
        regNoEditText = findViewById(R.id.regno_edittext);
        emailEditText = findViewById(R.id.email_edittext);
        passwordEditText = findViewById(R.id.password_edittext);
        confirmPasswordEditText = findViewById(R.id.confirmpassword_edittext);
        phoneNoEditText = findViewById(R.id.phoneno_edittext);
        signUpButton = findViewById(R.id.signup_button);
        loginButton = findViewById(R.id.login_button);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = nameEditText.getText().toString();
                regNo = regNoEditText.getText().toString();
                email = emailEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPassword = confirmPasswordEditText.getText().toString();
                phoneNo = phoneNoEditText.getText().toString();

                if(name == null || name.equals("")) {

                }

                if(validate()) {
                    searchUser();
                } else {
                    showToast("Enter Valid Data");
                }
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                showToast("Login to continue");
                startActivity(intent);
                finish();
            }
        });
    }

    //To check if the user already exists
    private void searchUser() {
        Helper.mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("DataSnapshot Data: ", dataSnapshot.toString());
                boolean contains = false;
                for(DataSnapshot ds : dataSnapshot.getChildren() ) {
                    if((ds.getKey()).equals(regNo)) {
                        showToast("Account already exists!");
                    } else {
                        signup();
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    //To add user details to firebase database
    private void signup() {
        Firebase userRef = Helper.mUserRef.child(regNo);//.push();
        String key = userRef.getKey();
        userRef.child("name").setValue(name);
        userRef.child("email").setValue(email);
        userRef.child("password").setValue(password);
        userRef.child("phone").setValue(phoneNo);
        showToast("Signup successful, login to proceed");
        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    //TODO : Complete this method
    //To validate the user data
    private boolean validate() {
        return true;
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_LONG).show();
    }
}
