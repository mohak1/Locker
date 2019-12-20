package com.fablab.locker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.fablab.locker.R;

import io.github.dreierf.materialintroscreen.MaterialIntroActivity;
import io.github.dreierf.materialintroscreen.MessageButtonBehaviour;
import io.github.dreierf.materialintroscreen.SlideFragmentBuilder;


public class IntroActivity extends MaterialIntroActivity {
//    SharedPreferences mSharedPreferences;
//    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //editor = getSharedPreferences(MY_PREF, MODE_PRIVATE).edit();

        //mSharedPreferences = getSharedPreferences(MY_PREF, MODE_PRIVATE);

        //String json = mSharedPreferences.getString("a", "");
        //String intro = mSharedPreferences.getString("intro", "");

//        if ("Something".equals("3")) {
//            if ("" != null) {
//                if ("Something".equals("1")) {
////                    Intent intent = new Intent(IntroActivity.this, HomeActivity.class);
////                    startActivity(intent);
////                    finish();
//                }
//            } else {
//                Intent intent = new Intent(IntroActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        }
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