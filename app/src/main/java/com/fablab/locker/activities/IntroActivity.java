package com.fablab.locker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fablab.locker.R;
import com.fablab.locker.SharedPref;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;


public class IntroActivity extends MaterialIntroActivity {
//    SharedPreferences mSharedPreferences;
//    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPref.init(getApplicationContext());

        if(SharedPref.read(SharedPref.REGNO, null) != null) {
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        }

        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.button_background)
                .title("Organize your Time with us")
                .description("Schedule")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.button_background)
                .title("Get goodies instantly with QR code")
                .description("QR Authorization")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.button_background)
                .title("Get updated with Links and Announcements")
                .description("Instant Announcements")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                .backgroundColor(R.color.colorPrimary)
                .buttonsColor(R.color.colorPrimaryDark)
                .image(R.drawable.button_background)
                .title("Our campus is huge")
                .description("Never get lost")
                .build()
        );
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.colorPrimary)
                        .buttonsColor(R.color.colorPrimaryDark)
                        .image(R.drawable.button_background)
                        .title("Lets get started!")
                        .description("Login now!")
                        .build(),
                new MessageButtonBehaviour(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent loginAction = new Intent(IntroActivity.this, LoginActivity.class);
                        startActivity(loginAction);
                        finish();
                    }
                }, "Join us!"));
    }
}