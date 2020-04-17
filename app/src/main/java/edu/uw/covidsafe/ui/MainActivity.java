package edu.uw.covidsafe.ui;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.fragment.app.FragmentTransaction;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.comms.NetworkConstant;
import edu.uw.covidsafe.ui.health.TipRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.HistoryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.ui.settings.PermUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import com.example.covidsafe.R;

import edu.uw.covidsafe.utils.Utils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;


public class MainActivity extends AppCompatActivity {

    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e("state","main activity oncreate");
        this.activity = this;
        setContentView(R.layout.activity_main);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.e("metadata","BUILD "+android.os.Build.VERSION.SDK_INT+"");
        Log.e("metadata","RELEASE "+android.os.Build.VERSION.RELEASE+"");
        Log.e("metadata","MANUFACTURER "+manufacturer);
        Log.e("metadata","MODEL "+model);

        AppCenter.start(getApplication(), Constants.AnalyticsSecret, Analytics.class, Crashes.class);
        Crashes.setEnabled(true);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_release);

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getString(R.string.onboard_enabled_pkey), false);
        editor.commit();

        Constants.MainTipAdapter = new TipRecyclerViewAdapter(this, this);
        Constants.DiagnosisTipAdapter = new TipRecyclerViewAdapter(this, this);
        Constants.NotificationAdapter = new NotifRecyclerViewAdapter(this,this);
        Constants.HistoryAdapter = new HistoryRecyclerViewAdapter(this,this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("demo","on new intent");
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionLogic.permissionLogic(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Constants.CurrentFragment.toString().toLowerCase().contains("settings")) {
                    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                    tx.setCustomAnimations(
                            R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                            R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                    tx.replace(R.id.fragment_container, Constants.MainFragment).commit();
                }
                else if (Constants.CurrentFragment.toString().toLowerCase().contains("diagnosis")) {
                    FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                    tx.setCustomAnimations(
                            R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                            R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                    tx.replace(R.id.fragment_container, Constants.HealthFragment).commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.e("state","onactivityresult "+requestCode+","+resultCode);
        boolean hasBlePerms = Utils.hasBlePermissions(getApplicationContext());
        if (requestCode == 0) {
            if (resultCode == -1) {
                if (hasBlePerms) {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                    editor.putBoolean(getApplicationContext().getString(R.string.ble_enabled_pkey), true);
                    editor.commit();
                    if (!Constants.LoggingServiceRunning) {
                        Utils.startLoggingService(this);
                        Log.e("ble","ble switch logic");
                        BluetoothUtils.startBle(this);
                        PermUtils.transition(false,this);
                    }
                    else {
                        Log.e("ble","ble switch logic2");
                        BluetoothUtils.startBle(this);
                    }
                }
            }
            else if (resultCode == 0) {
                Utils.updateSwitchStates(this);
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

        if (!Constants.LoggingServiceRunning) {
            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
            boolean gpsEnabled = prefs.getBoolean(getString(R.string.gps_enabled_pkey), false);
            boolean bleEnabled = prefs.getBoolean(getString(R.string.ble_enabled_pkey), false);
            if (gpsEnabled) {
                PermUtils.gpsSwitchLogic(this);
            }
            if (bleEnabled) {
                PermUtils.bleSwitchLogic(this);
            }
        }

        if (!Constants.PullServiceRunning) {
            Utils.startPullService(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("logme","activity destroyed");
        try {
            this.unregisterReceiver(BluetoothUtils.bluetoothReceiver);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    public void initView() {
        if (Constants.CurrentFragment != null &&
            !Constants.CurrentFragment.toString().toLowerCase().contains("permission")) {
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
                        if (Constants.MainFragmentState.toString().toLowerCase().contains("main")) {
                            selectedFragment = Constants.MainFragment;
                        }
                        else if (Constants.MainFragmentState.toString().toLowerCase().contains("settings")) {
                            selectedFragment = Constants.SettingsFragment;
                        }
                        break;
//                    case R.id.action_contact_log:
//                        selectedFragment = Constants.ContactLogFragment;
//                        break;
                    case R.id.action_report:
//                        Log.e("STate", "cur fragment "+Constants.CurrentFragment.toString());
                        if (Constants.ReportFragmentState.toString().toLowerCase().contains("diagnosis")) {
                            selectedFragment = Constants.DiagnosisFragment;
                        }
                        else {
                            selectedFragment = Constants.HealthFragment;
                        }
                        break;
                    case R.id.action_settings:
                        selectedFragment = Constants.FaqFragment;
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
