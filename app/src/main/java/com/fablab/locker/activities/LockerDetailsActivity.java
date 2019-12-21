package com.fablab.locker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.fablab.locker.Helper;
import com.fablab.locker.R;

public class LockerDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locker_details);

        Helper.firebaseInit();
    }
}
