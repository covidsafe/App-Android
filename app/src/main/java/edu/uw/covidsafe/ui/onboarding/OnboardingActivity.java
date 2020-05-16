package edu.uw.covidsafe.ui.onboarding;

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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.comms.NetworkConstant;
import edu.uw.covidsafe.ui.contact_trace.HumanOpsAsyncTask;
import edu.uw.covidsafe.gps.GpsOpsAsyncTask;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.preferences.AppPreferencesHelper;
import edu.uw.covidsafe.preferences.LocaleHelper;
import edu.uw.covidsafe.seed_uuid.SeedUUIDOpsAsyncTask;
import edu.uw.covidsafe.ui.symptoms.SymptomsOpsAsyncTask;
import edu.uw.covidsafe.ui.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.PermissionLogic;
import edu.uw.covidsafe.ui.settings.PermUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.TimeUtils;
import edu.uw.covidsafe.utils.Utils;

public class OnboardingActivity extends AppCompatActivity {

    boolean forceOnboard = false;
    Activity activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCenter.start(getApplication(), Constants.AnalyticsSecret, Analytics.class, Crashes.class);
        Crashes.setEnabled(true);

        this.activity = this;
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        Log.e("metadata","BUILD "+android.os.Build.VERSION.SDK_INT+"");
        Log.e("metadata","RELEASE "+android.os.Build.VERSION.RELEASE+"");
        Log.e("metadata","MANUFACTURER "+manufacturer);
        Log.e("metadata","MODEL "+model);
        if (Constants.DEBUG && !Constants.PUBLIC_DEMO) {
            insertDummyData();
        }

        Constants.init(this);
        CryptoUtils.keyInit(this);

        //seed doesn't yet exist
        //generate seed
        CryptoUtils.generateInitSeed(getApplicationContext(),false);

        NetworkConstant.init(this);
        this.registerReceiver(BluetoothUtils.bluetoothReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        SharedPreferences prefs = getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean b = prefs.getBoolean(getString(R.string.onboard_enabled_pkey),true);
        Log.e("onboarding","should start onboarding? "+b);
        if (b || forceOnboard) {
            setContentView(R.layout.activity_onboarding);
            if(AppPreferencesHelper.isOnboardingShownToUser(this)){
                Constants.pageNumber = 4;
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding,
                        Constants.PagerFragment).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding,
                        Constants.PagerFragment).commit();
            }
        }
        else {
            startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        }
    }

    public void insertDummyData() {
        Log.e("health","insert dummy data");
//        new SymptomsOpsAsyncTask(Constants.SymptomsDatabaseOps.DeleteAll, this).execute();
        new GpsOpsAsyncTask(Constants.GpsDatabaseOps.DeleteAll, this).execute();
        new SeedUUIDOpsAsyncTask(Constants.UUIDDatabaseOps.DeleteAll, this).execute();
        // DONT DO THIS OTHERWISE PULL DEMO TASK WILL FAIL!!
//        new BleOpsAsyncTask(this, Constants.BleDatabaseOps.DeleteAll).execute();
//        new HumanOpsAsyncTask(this, Constants.HumanDatabaseOps.DeleteAll).execute();

        SharedPreferences prefs = activity.getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(activity.getString(R.string.init_key_exists_pkey), false);
        editor.commit();

        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm aa");
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy/MM/dd");

        try {
            Date day1 = new Date(TimeUtils.getNDaysForward(0));
            Date day2 = new Date(TimeUtils.getNDaysForward(-1));
            Date day3 = new Date(TimeUtils.getNDaysForward(-3));
            Date day4 = new Date(TimeUtils.getNDaysForward(-4));

            Date d0 = format.parse(format2.format(day1)+" 1:22 AM");
            Date d1 = format.parse(format2.format(day1)+" 2:22 PM");
            Date d2 = format.parse(format2.format(day3)+" 1:11 AM");
            Date d3 = format.parse(format2.format(day4)+" 3:33 AM");
            Date d4 = format.parse(format2.format(day4)+" 4:44 PM");
//            Date d5 = format.parse(format2.format(day1)+" 2:22 PM");
            SymptomsRecord record0 = new SymptomsRecord();
            SymptomsRecord record1 = new SymptomsRecord();
            SymptomsRecord record2 = new SymptomsRecord();
            SymptomsRecord record3 = new SymptomsRecord();
            SymptomsRecord record4 = new SymptomsRecord();
            record0.setTs(d0.getTime());
//            record1.setTs(d5.getTime());
            record2.setTs(d2.getTime());
            record3.setTs(d3.getTime());
            record4.setTs(d4.getTime());

            record0.setFever(true);
            record1.setCough(true);
            record1.setVomiting(true);
            record2.setHeadache(true);
            record3.setTroubleBreathing(true);
            record4.setDiarrhea(true);
            new SymptomsOpsAsyncTask(this, record0).execute();
            new SymptomsOpsAsyncTask(this, record1).execute();
            new SymptomsOpsAsyncTask(this, record2).execute();
            new SymptomsOpsAsyncTask(this, record3).execute();
            new SymptomsOpsAsyncTask(this, record4).execute();
            double[] lats = new double[]{47.6537211,47.6536759,47.6358822,47.6306149,47.6221534};
            double[] lons = new double[]{-122.3080918,-122.3155732,-122.2954408,-122.2982472,-122.2793301};

            new GpsOpsAsyncTask(new GpsRecord(
                    d0.getTime(), lats[0],lons[0],"",this
            ), this).execute();
            new GpsOpsAsyncTask(new GpsRecord(
                    d1.getTime(), lats[1],lons[1],"",this
            ), this).execute();
            new GpsOpsAsyncTask(new GpsRecord(
                    d2.getTime(), lats[2],lons[2],"",this
            ), this).execute();
            new GpsOpsAsyncTask(new GpsRecord(
                    d3.getTime(), lats[3],lons[3],"",this
            ), this).execute();
            new GpsOpsAsyncTask(new GpsRecord(
                    d4.getTime(), lats[4],lons[4],"",this
            ), this).execute();

            new HumanOpsAsyncTask(this, "1234","Alice","", "alice@alice.com").execute();
            new HumanOpsAsyncTask(this, "5678","Bob","","bob@bob.com").execute();
            new HumanOpsAsyncTask(this, "5678","Charlie","","charlie@brown.com").execute();
            new HumanOpsAsyncTask(this, "5678","David","","david@goliath.com").execute();
            new HumanOpsAsyncTask(this, "5678","Ernie","","ernie@bert.com").execute();
            new HumanOpsAsyncTask(this, "5678","Francis","","francis@bacon.com").execute();
            new HumanOpsAsyncTask(this, "5678","George","","george@george.com").execute();
            new HumanOpsAsyncTask(this, "5678","Harry","","harry@harry.com").execute();
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
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
        try {
            this.unregisterReceiver(BluetoothUtils.bluetoothReceiver);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        Log.e("aa","onactivityresult "+requestCode+","+resultCode);
        //bluetooth is now turned on
        boolean hasBlePerms = Utils.hasBlePermissions(getApplicationContext());
        if (requestCode == 0) {
            if (resultCode == -1) {
                if (hasBlePerms) {
                    SharedPreferences.Editor editor = getApplicationContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                    AppPreferencesHelper.setBluetoothEnabled(this);
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
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
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
