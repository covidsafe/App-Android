package com.example.corona;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class MainFragment extends Fragment {

    Button trackButton;
    TextView tv1;
    TextView riskTv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if (Constants.DEBUG) {
            view = inflater.inflate(R.layout.fragment_main_debug, container, false);
        }
        else {
            view = inflater.inflate(R.layout.fragment_main_release, container, false);
        }

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.main_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Constants.CurrentFragment = this;
        Constants.MainFragment = this;

        tv1 = (TextView)getActivity().findViewById(R.id.textView);
        trackButton = (Button)getActivity().findViewById(R.id.trackButton);
        riskTv = (TextView)getActivity().findViewById(R.id.riskStatusTv);
//        riskTv.setText(getString(R.string.risk_low));
        updateUI();

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
                            ActivityCompat.requestPermissions(getActivity(), Constants.permissions, 1);
                        }
                        else if (Constants.BLUETOOTH_ENABLED && (Constants.blueAdapter == null || !Constants.blueAdapter.isEnabled())) {
                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            getActivity().startActivityForResult(enableBtIntent, 0);
                        }
                        else {
                            Utils.createNotificationChannel(getActivity());
                            getActivity().startService(new Intent(getActivity(), LocationService.class));
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
        if (Constants.tracking) {
            trackButton.setText("Stop logging");
            trackButton.setBackgroundResource(R.drawable.stopbutton);
        }
        else {
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
