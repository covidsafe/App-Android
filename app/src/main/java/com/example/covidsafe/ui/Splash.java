package com.example.covidsafe.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.covidsafe.R;
import com.example.covidsafe.utils.Constants;

public class Splash extends AppCompatActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
//
        setContentView(R.layout.splash);

        getSupportActionBar().hide();

        /* New Handler to start the Menu-Activity
         * and close this com.edushealth.earapp.Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(Splash.this, MainActivity.class);
                Splash.this.startActivity(mainIntent);
                Splash.this.finish();
            }
        }, Constants.SPLASH_DISPLAY_LENGTH);
//
    }
}
