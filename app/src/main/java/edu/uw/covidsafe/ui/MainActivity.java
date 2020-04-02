package edu.uw.covidsafe.ui;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import edu.uw.covidsafe.comms.SendInfectedUserData;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import com.example.covidsafe.R;
import edu.uw.covidsafe.utils.ServiceUtils;
import edu.uw.covidsafe.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private Context mContext;

    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("state","main activity oncreate");
        this.activity = this;
        setContentView(R.layout.activity_main);
        mContext = this;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.e("metadata","BUILD "+android.os.Build.VERSION.SDK_INT+"");
        Log.e("metadata","RELEASE "+android.os.Build.VERSION.RELEASE+"");
        Log.e("metadata","MANUFACTURER "+manufacturer);
        Log.e("metadata","MODEL "+model);

        AppCenter.start(getApplication(), Constants.AnalyticsSecret, Analytics.class, Crashes.class);
        Crashes.setEnabled(true);

//        CryptoUtils.keyInit(this);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
//        if (Constants.DEBUG) {
//            bottomNavigationView.getMenu().clear();
//            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_debug);
//        }
//        else {
            bottomNavigationView.getMenu().clear();
            bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_release);
//        }

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getString(R.string.onboard_enabled_pkey), false);
        editor.commit();

        //seed doesn't yet exist
        //generate seed
        CryptoUtils.generateInitSeed(getApplicationContext(), false);

//        ServiceUtils.scheduleLookupJob(mContext);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionLogic.permissionLogicOnboard(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
//        int result = data.getIntExtra(StartActivityForResult.this.getString(R.string.result), -1);
//        String msg = "requestCode=" + requestCode + ", resultCode=" + resultCode + ", result=" + result;
//        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        Log.e("aa","onactivityresult "+requestCode+","+resultCode);

        //bluetooth is now turned on
        if (requestCode == 0 && resultCode == -1) {
            if (Constants.startingToTrack) {
                Log.e("logme","starting to track");
                Utils.startBackgroundService(this);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state","main activity onresume");

        Constants.init(this);
        initView();
        initBottomNav();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("logme","activity destroyed");
    }

    public void initView() {
        if (Constants.CurrentFragment != null &&
            !Constants.CurrentFragment.toString().toLowerCase().contains("permission")) {
            Log.e("state","mainactivity - initview "+Constants.CurrentFragment.toString());
            if (Constants.CurrentFragment.toString().toLowerCase().contains("symptom") ||
                Constants.CurrentFragment.toString().toLowerCase().contains("diagnosis")) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.HealthFragment).commit();
            }
            else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.CurrentFragment).commit();
            }
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
                    case R.id.action_report:
                        selectedFragment = Constants.HealthFragment;
                        break;
                    case R.id.action_help:
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
