package com.fablab.locker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.client.Firebase;


public class MainActivity extends AppCompatActivity {

    private Firebase mRootRef, mUserRef, mLockerRef;

    private static final String ROOT_URL = "https://digilocker-dc8c1.firebaseio.com/";
    private static final String LOCKER_URL = "https://digilocker-dc8c1.firebaseio.com/Locker";
    private static final String  USER_URL = "https://digilocker-dc8c1.firebaseio.com/User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootRef = new Firebase(ROOT_URL);
        mUserRef = new Firebase(USER_URL);
        mLockerRef = new Firebase(LOCKER_URL);

        Button temp = findViewById(R.id.button);
        temp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser("16BCE0813");
            }
        });
    }

    private String addUser(String reg) {
        Firebase userRef = mUserRef.child(reg);//.push();
        String key = userRef.getKey();
        userRef.child("Name").setValue("Mohak");
        userRef.child("Mobile No").setValue("7568855448");
        userRef.child("Email").setValue("mohak@gmail.com");
        return key;
    }
}
