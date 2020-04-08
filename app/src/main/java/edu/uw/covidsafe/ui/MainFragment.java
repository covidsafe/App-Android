package edu.uw.covidsafe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.comms.PullFromServerTaskDemo;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifDbModel;
import edu.uw.covidsafe.ui.notif.NotifOpsAsyncTask;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.ui.settings.PermUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class MainFragment extends Fragment {

    View view;
    ImageView broadcastSwitch;
    TextView broadcastProp;
    TextView broadcastTitle;

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
                FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                tx.replace(R.id.fragment_container, Constants.SettingsFragment).commit();
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

        initTestButtons();

        broadcastProp = view.findViewById(R.id.broadcastProp);
        broadcastTitle = view.findViewById(R.id.broadcastTitle);

        broadcastSwitch = view.findViewById(R.id.switch1);
        broadcastSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
                boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
                boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);

                // flip switch to inverse of current broadcasting state
                broadcastSwitchLogic(!(gpsEnabled||bleEnabled));
            }
        });
        return view;
    }

    public void broadcastSwitchLogic(boolean isChecked) {
        Log.e("state","broadcast switch logic");
        if (isChecked) {
            PermUtils.gpsSwitchLogic(getActivity());
            PermUtils.bleSwitchLogic(getActivity());
        }
        else {
            Utils.haltLoggingService(getActivity(), view);
        }

        updateBroadcastUI();
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
        boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
        boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);

        broadcastSwitchLogic(gpsEnabled||bleEnabled);
    }

    public void updateBroadcastUI() {
        Log.e("state","update broadcast ui");
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
        boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
        if (gpsEnabled || bleEnabled) {
            broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_on));
            broadcastTitle.setText("Broadcasting On");
            Utils.linkify(broadcastProp,getString(R.string.logging));
        }
        else {
            broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_off));
            broadcastTitle.setText("Broadcasting Off");
            Utils.linkify(broadcastProp,getString(R.string.stopping));
        }
    }

    public void initTestButtons() {
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
//                Utils.notif2(getContext(),"",""");
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
//                Thread r = new Thread(new PullFromServerTaskDemo(getContext()));
//                r.start();

                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                getActivity().startActivity(intent);
            }
        });

        bb.setEnabled(false);
        bb.setVisibility(View.GONE);
        b3b.setEnabled(false);
        b3b.setVisibility(View.GONE);
    }
}
