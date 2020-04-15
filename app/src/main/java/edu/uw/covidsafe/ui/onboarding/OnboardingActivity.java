package edu.uw.covidsafe.ui.onboarding;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

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

import com.example.covidsafe.R;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.comms.NetworkConstant;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.PermissionLogic;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;

public class OnboardingActivity extends AppCompatActivity {

    boolean forceOnboard = false;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = this;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.e("metadata","BUILD "+android.os.Build.VERSION.SDK_INT+"");
        Log.e("metadata","RELEASE "+android.os.Build.VERSION.RELEASE+"");
        Log.e("metadata","MANUFACTURER "+manufacturer);
        Log.e("metadata","MODEL "+model);

        Constants.init(this);
        CryptoUtils.keyInit(this);

        //seed doesn't yet exist
        //generate seed
        CryptoUtils.generateInitSeed(getApplicationContext());

        NetworkConstant.init(this);
        this.registerReceiver(BluetoothUtils.bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean b = prefs.getBoolean(getString(R.string.onboard_enabled_pkey),true);
        Log.e("onboarding","should start onboarding? "+b);
        if (b || forceOnboard) {
            setContentView(R.layout.activity_onboarding);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding,
                    Constants.PagerFragment).commit();
        }
        else {
            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e("state","MAIN ON OPTIONS "+Constants.pageNumber);
        switch (item.getItemId()) {
            case android.R.id.home:
                Constants.pageNumber = 4;
                FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right,
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                tx.replace(R.id.fragment_container_onboarding, Constants.PagerFragment).commit();
                return true;
        }
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        PermissionLogic.permissionLogic(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("logme","activity destroyed");
        this.unregisterReceiver(BluetoothUtils.bluetoothReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.e("aa","onactivityresult "+requestCode+","+resultCode);
        //bluetooth is now turned on

//        Switch bleSwitch = (Switch) findViewById(R.id.bleSwitch);
//        if (requestCode == 0 && resultCode == 0) {
//            if (bleSwitch != null) {
//                bleSwitch.setChecked(false);
//                Constants.BLUETOOTH_ENABLED = false;
//                SharedPreferences.Editor editor = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
//                editor.putBoolean(getString(R.string.ble_enabled_pkey), false);
//                editor.commit();
//            }
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("state","onboarding onresume");

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean b = prefs.getBoolean(getString(R.string.onboard_enabled_pkey),true);
        if (!b && !forceOnboard) {
            finish();
        }

//        if (Constants.CurrentFragment != null) {
//            Log.e("state","mainactivity - initview "+Constants.CurrentFragment.toString());
//            if (Constants.CurrentFragment.toString().toLowerCase().contains("start") ||
//                Constants.CurrentFragment.toString().toLowerCase().contains("permission")) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding, Constants.CurrentFragment).commit();
//            }
//        }
//        else {
//            SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
//            boolean b = prefs.getBoolean(getString(R.string.onboard_enabled_pkey), true);
//            Log.e("onboarding", "should start onboarding? " + b);
//            if (b) {
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding, new StartFragment()).commit();
//            }
//        }
    }
}
