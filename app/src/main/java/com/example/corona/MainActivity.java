package com.example.corona;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
public class MainActivity extends AppCompatActivity {
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        setContentView(R.layout.activity_main);
        CryptoUtils.keyInit(this);
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
        PermissionLogic.permissionLogic(requestCode, permissions, grantResults, this);
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

    public void reset(View v) {
        Utils.clearPreferences(this);
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
