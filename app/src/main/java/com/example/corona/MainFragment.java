package com.example.corona;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

public class MainFragment extends Fragment {

    Button trackButton;
    Button uploadGpsButton;
    Button uploadBleButton;
    TextView tv1;
    TextView gpsResults;
    TextView bleResults;
    TextView riskTv;
    Handler serviceHandler;
    int gpsLines = 0;
    int bleLines = 0;

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

        gpsResults = (TextView)getActivity().findViewById(R.id.gpsResults);
        gpsResults.setText("");
        gpsResults.setMovementMethod(new ScrollingMovementMethod());

        bleResults = (TextView)getActivity().findViewById(R.id.bleResults);
        bleResults.setText("");
        bleResults.setMovementMethod(new ScrollingMovementMethod());

        trackButton = (Button)getActivity().findViewById(R.id.trackButton);
        uploadGpsButton = (Button)getActivity().findViewById(R.id.uploadGpsButton);
        uploadBleButton = (Button)getActivity().findViewById(R.id.uploadBleButton);
        riskTv = (TextView)getActivity().findViewById(R.id.riskStatusTv);
//        riskTv.setText(getString(R.string.risk_low));
        updateUI();


        uploadGpsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new UploadAllGpsTask(getActivity(), getActivity()), 0);
            }
        });

        uploadBleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Handler handler = new Handler();
                handler.postDelayed(new UploadAllBleTask(getActivity(), getActivity()), 0);
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

                            bleResults.setText("");
                            gpsResults.setText("");

                            Utils.createNotificationChannel(getActivity());

                            serviceHandler = new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    Bundle reply = msg.getData();
                                    String out1 = reply.getString("gps");
                                    String out2 = reply.getString("ble");
                                    if (out1!=null) {
                                        if (gpsLines > 20) {
                                            String ss = gpsResults.getText().toString();
                                            int ii = ss.indexOf("\n");
                                            String oo = ss.substring(ii+1,ss.length()) + out1+"\n";
                                            gpsResults.setText(oo);
                                        }
                                        else {
                                            gpsResults.append(out1 + "\n");
                                        }
                                        gpsLines+=1;
                                    }
                                    if (out2!=null) {
                                        if (bleLines > 20) {
                                            String ss = bleResults.getText().toString();
                                            int ii = ss.indexOf("\n");
                                            String oo = ss.substring(ii+1,ss.length()) + out2+"\n";
                                            bleResults.setText(oo);
                                        }
                                        else {
                                            bleResults.append(out2 + "\n");
                                        }
                                        bleLines+=1;
                                    }
                                }
                            };

                            Intent intent = new Intent(getActivity(), LocationService.class);
                            intent.putExtra("messenger", new Messenger(serviceHandler));
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
