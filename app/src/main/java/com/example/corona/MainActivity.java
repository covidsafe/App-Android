package com.example.corona;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
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
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {


        Log.e("logme","on request permission "+requestCode);
        for (int i = 0; i < grantResults.length; i++) {
            Log.e("logme","grant results "+permissions[i]+","+grantResults[i]);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_DENIED) {
            boolean shouldAsk = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION);
            Log.e("logme","does not have permissions "+shouldAsk);
            if (requestCode == 1 && shouldAsk) {
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setTitle("Permission denied")
                        .setMessage(getString(R.string.perm_rationale))
                        .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION}, 2);
                            }
                        })
                        .setPositiveButton(R.string.sure, null)
                        .setCancelable(false).create();
                dialog.show();
            }
            else if (!shouldAsk){
                AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                        .setTitle("Permission denied")
                        .setMessage(getString(R.string.perm_ask))
                        .setNegativeButton(R.string.no, null)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        })
                        .setCancelable(false).create();
                dialog.show();
            }
        }
        else {
            Log.e("logme","has permissions");
            if (Constants.startingToTrack) {
                Log.e("logme","starting to track");
                Utils.createNotificationChannel(this);
                startService(new Intent(this, LocationService.class));
                Constants.tracking = true;
                Constants.startingToTrack = false;
                Button trackButton = (Button)findViewById(R.id.trackButton);
                trackButton.setText("Stop tracking");
                trackButton.setBackgroundResource(R.drawable.stopbutton);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Constants.init();
        initView();
        initBottomNav();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("logme","activity destroyed");
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
//                        Log.e("logme","WARNING-nav");
                        selectedFragment = Constants.WarningFragment;
                        break;
                    case R.id.action_help:
//                        Log.e("logme","HELP-nav");
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
