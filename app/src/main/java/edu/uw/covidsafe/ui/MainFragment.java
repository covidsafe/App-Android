package edu.uw.covidsafe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import edu.uw.covidsafe.LoggingService;
import edu.uw.covidsafe.ble.BluetoothScanHelper;
import edu.uw.covidsafe.ble.BluetoothServerHelper;
import edu.uw.covidsafe.ble.BluetoothUtils;
import edu.uw.covidsafe.ui.health.CardRecyclerViewAdapter;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class MainFragment extends Fragment {

    View view;
    Switch broadcastSwitch;
    TextView broadcastProp;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView rview = view.findViewById(R.id.recyclerViewDiagnosis);
        CardRecyclerViewAdapter adapter = new CardRecyclerViewAdapter(getActivity(), getActivity());
        rview.setAdapter(adapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        ImageView settings = (ImageView) view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.SettingsFragment).commit();
            }
        });

        RecyclerView rview2 = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter adapter2 = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        rview2.setAdapter(adapter2);
        rview2.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rview3 = view.findViewById(R.id.recyclerViewNotifs);
        Constants.NotificationAdapter = new NotifRecyclerViewAdapter(getContext(),getActivity());
        rview3.setAdapter(Constants.NotificationAdapter);
        rview3.setLayoutManager(new LinearLayoutManager(getActivity()));

        broadcastProp = view.findViewById(R.id.broadcastProp);

        broadcastSwitch = view.findViewById(R.id.switch1);
        broadcastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
                boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
                boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);

                updateBroadcastUI();
                if (isChecked) {
                    // dummy code
                    Constants.NotificationAdapter.notifyUser(getString(R.string.exposed_notif));

                    if (!gpsEnabled && !bleEnabled) {
                        Utils.mkSnack(getActivity(), view, getString(R.string.prompt_to_enable_error));
                        broadcastSwitch.setChecked(false);
                    }
                    else {
                        Utils.startLoggingService(getActivity());
                    }
                }
                else {
                    Utils.haltLoggingService(getActivity(), view);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","main fragment on resume "+Constants.PullServiceRunning);
        Constants.CurrentFragment = this;
        Constants.MainFragment = this;
        Constants.MainFragmentState = this;

        if (!Constants.PullServiceRunning) {
            Utils.startPullService(getActivity());
        }

        updateBroadcastUI();
    }

    public void updateBroadcastUI() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
        boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
        if (gpsEnabled && bleEnabled) {
            if (broadcastSwitch.isChecked()) {
                broadcastProp.setText("Location sharing and bluetooth tracing are turned on");
            }
            else {
                broadcastProp.setText("Location sharing and bluetooth tracing are turned off");
            }
        }
        if (!gpsEnabled && !bleEnabled) {
            broadcastProp.setText("Location sharing and bluetooth tracing are turned off");
        }
        if (gpsEnabled && !bleEnabled) {
            if (broadcastSwitch.isChecked()) {
                broadcastProp.setText("Location sharing is turned on and bluetooth tracing is turned off");
            }
            else {
                broadcastProp.setText("Location sharing and bluetooth tracing are turned off");
            }
        }
        if (!gpsEnabled && bleEnabled) {
            if (broadcastSwitch.isChecked()) {
                broadcastProp.setText("Location sharing is turned off and bluetooth tracing is turned on");
            }
            else {
                broadcastProp.setText("Location sharing and bluetooth tracing are turned off");
            }
        }
    }
}
