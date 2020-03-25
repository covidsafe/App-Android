package com.example.covidsafe.ui;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Messenger;
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

import com.example.covidsafe.ble.BleOpsAsyncTask;
import com.example.covidsafe.gps.GpsOpsAsyncTask;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.gps.LocationService;
import com.example.covidsafe.R;
import com.example.covidsafe.utils.Utils;

public class MainFragment extends Fragment {

    Button trackButton;
    Button uploadGpsButton;
    Button uploadBleButton;
    TextView tv1;
    TextView riskTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
//        if (Constants.DEBUG) {
//            view = inflater.inflate(R.layout.fragment_main_debug, container, false);
//        }
//        else {
            view = inflater.inflate(R.layout.fragment_main_release, container, false);
//        }

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.main_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Switch sw1 = (Switch)getActivity().findViewById(R.id.switch1);
        sw1.setChecked(Constants.BLUETOOTH_ENABLED);
        sw1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Constants.BLUETOOTH_ENABLED = isChecked;
            }
        });

        Constants.CurrentFragment = this;
        Constants.MainFragment = this;

        tv1 = (TextView)getActivity().findViewById(R.id.textView);

        Utils.gpsResults = (TextView)getActivity().findViewById(R.id.gpsResults);
        Utils.gpsResults.setText("");
        Utils.gpsResults.setMovementMethod(new ScrollingMovementMethod());

        Utils.bleResults = (TextView)getActivity().findViewById(R.id.bleResults);
        Utils.bleResults.setText("");
        Utils.bleResults.setMovementMethod(new ScrollingMovementMethod());

        trackButton = (Button)getActivity().findViewById(R.id.trackButton);
        uploadGpsButton = (Button)getActivity().findViewById(R.id.uploadGpsButton);
        uploadBleButton = (Button)getActivity().findViewById(R.id.uploadBleButton);
        riskTv = (TextView)getActivity().findViewById(R.id.riskStatusTv);
//        riskTv.setText(getString(R.string.risk_low));
        updateUI();

        uploadGpsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                new GpsDbAsyncTask(getContext(), new GpsDbRecord(System.currentTimeMillis(),42,43, LocationManager.NETWORK_PROVIDER)).execute();
                new GpsOpsAsyncTask(getActivity()).execute();
            }
        });

        uploadBleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                new BleDbAsyncTask(getContext(), new BleDbRecord("1234",System.currentTimeMillis(),false,false)).execute();
                new BleOpsAsyncTask(getActivity()).execute();
            }
        });

        trackButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!Constants.tracking) {
                    Constants.startingToTrack = true;
                    try {
                        Log.e("logme","start service");

                        BluetoothManager bluetoothManager =
                                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
                        Constants.blueAdapter = bluetoothManager.getAdapter();

                        if (!Utils.hasPermissions(getActivity())) {
                            Log.e("aa","PERMS");
                            ActivityCompat.requestPermissions(getActivity(), Constants.permissions, 1);
                        }

                        if (Utils.hasPermissions(getActivity()) &&
                            Constants.BLUETOOTH_ENABLED && (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                            Log.e("aa","BLE");
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            getActivity().startActivityForResult(enableBtIntent, 0);
                        }

                        boolean hasPerms = Utils.hasPermissions(getActivity());
                        Log.e("aa","has perms?" +hasPerms);
                        Log.e("aa","ble status "+Constants.blueAdapter+","+Constants.blueAdapter.isEnabled());
                        if (hasPerms &&
                            (Constants.BLUETOOTH_ENABLED && Constants.blueAdapter != null && Constants.blueAdapter.isEnabled()) ||
                            (!Constants.BLUETOOTH_ENABLED)) {
                            Log.e("aa","START");

                            Utils.bleResults.setText("");
                            Utils.gpsResults.setText("");

                            Utils.createNotificationChannel(getActivity());

                            Intent intent = new Intent(getActivity(), LocationService.class);
                            intent.putExtra("messenger", new Messenger(Utils.serviceHandler));
                            getActivity().startService(intent);

                            Constants.tracking = true;
                            Constants.startingToTrack = false;
                        }
                    } catch (SecurityException e) {
                        Log.e("log", e.getMessage());
                    }
                }
                else {
                    Log.e("logme","stop service");
                    getActivity().stopService(new Intent(getActivity(), LocationService.class));
                    if (Constants.uploadTask!=null) {
                        Constants.uploadTask.cancel(true);
                    }
                    Constants.tracking = false;
                }
                updateUI();
            }
        });
    }

    public void updateUI() {
        Log.e("aa","updateui");
        if (Constants.tracking) {
            Log.e("aa","yes");
            trackButton.setText("Stop logging");
            trackButton.setBackgroundResource(R.drawable.stopbutton);
        }
        else {
            Log.e("aa","no");
            trackButton.setText("Start logging");
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
