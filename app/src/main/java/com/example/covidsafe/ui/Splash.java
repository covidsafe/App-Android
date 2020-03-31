package com.example.covidsafe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covidsafe.R;
import com.example.covidsafe.ui.onboarding.OnboardingActivity;
import com.example.covidsafe.utils.Constants;

public class Splash extends AppCompatActivity {

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//
        setContentView(R.layout.splash);

        getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(Splash.this, OnboardingActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, Constants.SPLASH_DISPLAY_LENGTH);
//
    }
}
