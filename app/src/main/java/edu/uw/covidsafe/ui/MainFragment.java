package edu.uw.covidsafe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.comms.PullFromServerTaskDemo;
import edu.uw.covidsafe.ui.health.TipRecyclerViewAdapter;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.HistoryRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifDbModel;
import edu.uw.covidsafe.ui.notif.NotifOpsAsyncTask;
import edu.uw.covidsafe.ui.notif.NotifRecord;
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

        RecyclerView tipView = view.findViewById(R.id.recyclerViewTips);
        tipView.setAdapter(Constants.TipAdapter);
        tipView.setLayoutManager(new LinearLayoutManager(getActivity()));

        ImageView settings = (ImageView) view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.SettingsFragment).commit();
            }
        });

        RecyclerView resourceView = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter resourceAdapter = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        resourceView.setAdapter(resourceAdapter);
        resourceView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView notifView = view.findViewById(R.id.recyclerViewNotifs);
        notifView.setAdapter(Constants.NotificationAdapter);
        notifView.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView historyView = view.findViewById(R.id.recyclerViewHistory);
        historyView.setAdapter(Constants.HistoryAdapter);
        historyView.setLayoutManager(new LinearLayoutManager(getActivity()));

        NotifDbModel model = ViewModelProviders.of(getActivity()).get(NotifDbModel.class);
        model.getAllSorted().observe(getActivity(), new Observer<List<NotifRecord>>() {
            @Override
            public void onChanged(List<NotifRecord> notifRecords) {
                //something in db has changed, update
                List<NotifRecord> currentNotifs = new LinkedList<>();
                List<NotifRecord> historyNotifs = new LinkedList<>();
                for (NotifRecord notif : notifRecords) {
                    if (notif.getCurrent()) {
                        currentNotifs.add(notif);
                    }
                    else {
                        historyNotifs.add(notif);
                    }
                }
                Constants.HistoryAdapter.setRecords(historyNotifs, view);
                Constants.NotificationAdapter.setRecords(currentNotifs, view);
                Constants.TipAdapter.enableTips(notifRecords.size(), view);
            }
        });

        Button bb = (Button)view.findViewById(R.id.button6);
        bb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NotifOpsAsyncTask(getContext(), new NotifRecord(System.currentTimeMillis(),
                        System.currentTimeMillis(), getString(R.string.default_exposed_notif), Constants.MessageType.Exposure.ordinal(),true)).execute();
//                Utils.notif(getContext());
            }
        });

        Button b2b = (Button)view.findViewById(R.id.button9);
        b2b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NotifOpsAsyncTask(getContext(),Constants.NotifDatabaseOps.DeleteAll).execute();
            }
        });

        Button b3b = (Button)view.findViewById(R.id.button8);
        b3b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new NotifOpsAsyncTask(getContext(), new NotifRecord(System.currentTimeMillis(),
//                        System.currentTimeMillis(), "hello history", Constants.MessageType.Exposure.ordinal(),false)).execute();
                Utils.notif2(getContext());
            }
        });

        Button b5b = (Button)view.findViewById(R.id.button10);
        b5b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String ss = model.records.getValue().size()+"";
//                for (int i = 0; i < model.records.getValue().size(); i++) {
//                    ss+=model.records.getValue().get(i).current;
//                }
//                Toast.makeText(getContext(), "notif size "+ss,Toast.LENGTH_LONG).show();
                Thread r = new Thread(new PullFromServerTaskDemo(getContext()));
                r.start();
            }
        });

        broadcastProp = view.findViewById(R.id.broadcastProp);

        broadcastSwitch = view.findViewById(R.id.switch1);
        broadcastSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                broadcastSwitchLogic(isChecked);
            }
        });

        return view;
    }

    public void broadcastSwitchLogic(boolean isChecked) {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
        boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);

        updateBroadcastUI();
        if (isChecked) {
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

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","main fragment on resume "+Constants.PullServiceRunning+","+Constants.LoggingServiceRunning);
        Constants.CurrentFragment = this;
        Constants.MainFragment = this;
        Constants.MainFragmentState = this;

        if (!Constants.PullServiceRunning) {
            Utils.startPullService(getActivity());
        }

        updateBroadcastUI();

        // resume broadcasting if it was switched on, but perhaps user restarted phone or previously killed service
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        if (prefs.getBoolean(getString(R.string.broadcasting_enabled_pkey), false)) {
            Log.e("state","rebroadcast is true");
            broadcastSwitch.setChecked(true);
            broadcastSwitchLogic(true);
        }
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
