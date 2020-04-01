package edu.uw.covidsafe.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import edu.uw.covidsafe.ble.BluetoothScanHelper;
import edu.uw.covidsafe.ble.BluetoothServerHelper;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.BackgroundService;
import com.example.covidsafe.R;
import edu.uw.covidsafe.utils.Utils;

import java.util.UUID;

public class MainFragment extends Fragment {

    Button trackButton;
    TextView bleBeaconId;
    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        if (Constants.DEBUG) {
//            view = inflater.inflate(R.layout.fragment_main_debug, container, false);
//        }
//        else {
            view = inflater.inflate(R.layout.fragment_main, container, false);
//        }

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.main_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("logme","main fragment onresume");

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);


        Switch bleSwitch = (Switch)getActivity().findViewById(R.id.bleSwitch);
        bleSwitch.setChecked(prefs.getBoolean("bleEnabled", Constants.BLUETOOTH_ENABLED));

        bleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.BLUETOOTH_ENABLED = isChecked;
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean("bleEnabled", isChecked);
                editor.commit();
            }
        });

        Switch gpsSwitch = (Switch)getActivity().findViewById(R.id.gpsSwitch);
        gpsSwitch.setChecked(prefs.getBoolean("gpsEnabled", Constants.GPS_ENABLED));

        gpsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.GPS_ENABLED = isChecked;
                SharedPreferences.Editor editor = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE).edit();
                editor.putBoolean("gpsEnabled", isChecked);
                editor.commit();
            }
        });

        Constants.CurrentFragment = this;
        Constants.MainFragment = this;

        bleBeaconId = (TextView)getActivity().findViewById(R.id.uuidView);

        Utils.gpsResults = (TextView)getActivity().findViewById(R.id.gpsResults);
        Utils.gpsResults.setText("");
        Utils.gpsResults.setMovementMethod(new ScrollingMovementMethod());

        Utils.bleResults = (TextView)getActivity().findViewById(R.id.bleResults);
        Utils.bleResults.setText("");
        Utils.bleResults.setMovementMethod(new ScrollingMovementMethod());

        Utils.bleBeaconId = (TextView)getActivity().findViewById(R.id.uuidView);

        trackButton = (Button)getActivity().findViewById(R.id.trackButton);
        updateUI();

        Constants.contactUUID = UUID.randomUUID();
        bleBeaconId.setText(Constants.contactUUID.toString());

        trackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!Constants.tracking) {
                    Utils.gpsLines = 0;
                    Utils.bleLines = 0;
                    Constants.startingToTrack = true;
                    try {
                        Log.e("logme","start service");

                        BluetoothManager bluetoothManager =
                                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                        Constants.blueAdapter = bluetoothManager.getAdapter();

                        if (Constants.BLUETOOTH_ENABLED && !Utils.hasBlePermissions(getActivity())) {
                            Log.e("aa","NO BLE PERMS");
                            ActivityCompat.requestPermissions(getActivity(), Constants.blePermissions, 1);
                        }

                        if (Utils.hasBlePermissions(getActivity()) &&
                            Constants.BLUETOOTH_ENABLED && (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                            Log.e("aa","BLE");
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            getActivity().startActivityForResult(enableBtIntent, 0);
                        }

                        if (Constants.GPS_ENABLED && !Utils.hasGpsPermissions(getActivity())) {
//                            if ((Constants.GPS_ENABLED || Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) && !Utils.hasGpsPermissions(getActivity())) {
                            Log.e("aa","PERMS");
                            ActivityCompat.requestPermissions(getActivity(), Constants.gpsPermissions, 2);
                        }

//                        if ((Constants.GPS_ENABLED || Constants.BLUETOOTH_ENABLED) && Utils.permCheck(getActivity())) {
                        if ((Constants.GPS_ENABLED && Utils.gpsCheck(getActivity())) ||
                            (Constants.BLUETOOTH_ENABLED && Utils.bleCheck(getActivity()))) {
                            Utils.startBackgroundService(getActivity());
                        }

                        if (!Constants.GPS_ENABLED && !Constants.BLUETOOTH_ENABLED) {
                            Utils.mkSnack(getActivity(), view, getString(R.string.prompt_to_enable_error));
                            gpsSwitch.setEnabled(false);
                            bleSwitch.setEnabled(false);
                        }

                    } catch (SecurityException e) {
                        Log.e("log", e.getMessage());
                    }
                }
                else {
                    Log.e("logme","stop service");
                    getActivity().stopService(new Intent(getActivity(), BackgroundService.class));

                    if (Constants.mLocationManager != null) {
                        try {
                            Constants.mLocationManager.removeUpdates(Constants.locListeners[0]);
                            Constants.mLocationManager.removeUpdates(Constants.locListeners[1]);
                        } catch (Exception ex) {
                            Log.e("logme", "fail to remove location listners, ignore", ex);
                        }
                    }

                    Constants.blueAdapter.getBluetoothLeAdvertiser().stopAdvertising(BluetoothScanHelper.advertiseCallback);
                    BluetoothUtils.finishScan(getContext());
                    if (Constants.uploadTask!=null) {
                        Constants.uploadTask.cancel(true);
                    }
                    if (Constants.uuidGeneartionTask!=null) {
                        Constants.uuidGeneartionTask.cancel(true);
                    }
                    BluetoothServerHelper.stopServer();
                    try {
                        getContext().unregisterReceiver(BluetoothUtils.mReceiver);
                    }
                    catch(Exception e) {
                        Log.e("frag","unregister fail");
                    }
                    Constants.tracking = false;
                    gpsSwitch.setEnabled(true);
                    bleSwitch.setEnabled(true);
                }
                updateUI();
            }
        });
    }

    public void updateUI() {
        Log.e("aa","updateui");
        if (Constants.tracking) {
            Log.e("aa","yes");
            trackButton.setText(getString(R.string.stop));
            trackButton.setBackgroundResource(R.drawable.stopbutton);
        }
        else {
            Log.e("aa","no");
            trackButton.setText(getString(R.string.start));
            trackButton.setBackgroundResource(R.drawable.startbutton);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.MainFragment = this;
        Constants.CurrentFragment = this;
    }
}
