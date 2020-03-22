package com.example.corona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (Constants.DEBUG) {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_debug);
        }
        else {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_release);
        }

        requestPermissions();
    }

    public void requestPermissions() {
        if (!Utils.hasPermissions(this)) {
            Log.e("logme", "no perms");
            ActivityCompat.requestPermissions(this, Constants.permissions, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this code block should only execute if the user pressed the button
        if (!Utils.hasPermissions(this)) {
            Toast.makeText(this, "Permissions must be enabled before measurement can take place. Please try again.", Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(this, Constants.permissions, 1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants.init();
        initView();
        initBottomNav();
    }

    public void initView() {
        if (Constants.CurrentFragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.CurrentFragment).commit();
        } else {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.MainFragment).commit();
        }
    }

    public void initBottomNav() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_track:
                        selectedFragment = Constants.MainFragment;
                        break;
                    case R.id.action_history:
                        selectedFragment = Constants.HistoryFragment;
                        break;
                    case R.id.action_report:
                        selectedFragment = Constants.ReportFragment;
                        break;
                    case R.id.action_warning:
                        Log.e("logme","WARNING-nav");
                        selectedFragment = Constants.WarningFragment;
                        break;
                    case R.id.action_help:
                        Log.e("logme","HELP-nav");
                        selectedFragment = Constants.HelpFragment;
                        break;
                    case R.id.action_settings:
                        selectedFragment = Constants.SettingsFragment;
                        break;
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                }
                return true;
            }
        });
    }
}
