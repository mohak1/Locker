package com.fablab.locker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fablab.locker.R;
import com.firebase.client.Firebase;


public class MainActivity extends AppCompatActivity {

    private Firebase mRootRef, mUserRef, mLockerRef;

    private static final String ROOT_URL = "https://digilocker-dc8c1.firebaseio.com/";
    private static final String LOCKER_URL = "https://digilocker-dc8c1.firebaseio.com/Locker";
    private static final String  USER_URL = "https://digilocker-dc8c1.firebaseio.com/User";

    Button getALockerButton, lockButton, openButton;
    TextView lockerStatusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRootRef = new Firebase(ROOT_URL);
        mUserRef = new Firebase(USER_URL);
        mLockerRef = new Firebase(LOCKER_URL);

        Button getALockerButton = findViewById(R.id.getalocker_button);
        final Button lockButton = findViewById(R.id.getalocker_button);
        Button openButton = findViewById(R.id.getalocker_button);

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

    private void getALocker() {

    }

    private void lockLocker() {

    }

    private void unlockLocker() {

    }
}
