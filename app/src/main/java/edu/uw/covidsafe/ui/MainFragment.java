package edu.uw.covidsafe.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.hcp.SubmitNarrowcastMessageTask;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifDbModel;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.ui.settings.PermUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class MainFragment extends Fragment {

    View view;
    ImageView broadcastSwitch;
    ImageView broadcastRing;
    TextView broadcastProp;
    TextView broadcastTitle;
    ImageView refresh;
    SwipeRefreshLayout swipeLayout;
    TextView lastUpdated;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        view = inflater.inflate(R.layout.fragment_main, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        refresh = (ImageView) view.findViewById(R.id.refresh);
        lastUpdated = (TextView) view.findViewById(R.id.lastUpdated);

        RecyclerView tipView = view.findViewById(R.id.recyclerViewTips);
        tipView.setAdapter(Constants.TipAdapter);
        tipView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeLayout = view.findViewById(R.id.swiperefresh);
        swipeLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshTask();
                    }
                }
        );

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshTask();
            }
        });

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

        broadcastSwitch = view.findViewById(R.id.powerButton);
        broadcastRing = view.findViewById(R.id.ring);
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

    public void refreshTask() {
        Log.e("refresh","freshtask");
        new PullFromServerTask(getContext(),view).execute();
//        new PullFromServerTaskDemo(getContext(),getActivity(),view).execute();
        RotateAnimation rotate = new RotateAnimation(0,360,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1000);
        rotate.setRepeatCount(Animation.INFINITE);
        rotate.setRepeatMode(Animation.INFINITE);
        rotate.setInterpolator(new LinearInterpolator());
        refresh.startAnimation(rotate);
    }

    public void broadcastSwitchLogic(boolean isChecked) {
        Log.e("state","broadcast switch logic");
        if (isChecked) {
            PermUtils.gpsSwitchLogic(getActivity());
            PermUtils.bleSwitchLogic(getActivity());
        }
        else {
            Utils.haltLoggingService(getActivity(), view);
            PermUtils.transition(true, getActivity());
        }

        updateBroadcastUI(false);
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

        updateBroadcastUI(true);

        swipeLayout.setRefreshing(false);
        refresh.clearAnimation();

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        long ts = prefs.getLong(getActivity().getString(R.string.last_refresh_date_pkey), 0);
        if (ts != 0) {
            SimpleDateFormat format = new SimpleDateFormat("h:MM a");
            lastUpdated.setText("Last updated: "+format.format(new Date(ts)));
        }
        else {
            lastUpdated.setText("");
            lastUpdated.setVisibility(View.GONE);
        }
    }

    public void updateBroadcastUI(boolean updateSwitch) {
        Log.e("state","update broadcast ui");
        boolean hasGpsPerms = Utils.hasGpsPermissions(getActivity());
        boolean hasBlePerms = Utils.hasBlePermissions(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
        boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);

        if ((!hasGpsPerms && !hasBlePerms) || (!gpsEnabled && !bleEnabled)) {
            editor.putBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
            editor.putBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
            editor.commit();

            if (updateSwitch) {
                Log.e("transition","set to off");
                broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_off));
                broadcastRing.setAlpha(0f);
            }

            broadcastTitle.setText("Broadcasting Off");
            Utils.linkify(broadcastProp,getString(R.string.stopping));
        }
        else if (!hasGpsPerms) {
            editor.putBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
            editor.commit();
        }
        else if (!hasBlePerms) {
            editor.putBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
            editor.commit();
        }
        else if (gpsEnabled || bleEnabled) {
            if (updateSwitch) {
                Log.e("transition","set to on");
                broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_on));
            }
            broadcastTitle.setText("Broadcasting On");
            broadcastRing.setAlpha(1f);
            Utils.linkify(broadcastProp,getString(R.string.logging));
        }
    }


    public void initTestButtons() {
        Button b2b = (Button)view.findViewById(R.id.button9);
        b2b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                new NotifOpsAsyncTask(getContext(),Constants.NotifDatabaseOps.DeleteAll).execute();
//                SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
//                boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
//                boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
//
//                Log.e("sensor","status "+gpsEnabled+","+bleEnabled);
//                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
//                intent.setData(uri);
//                getActivity().startActivity(intent);

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

                List<Double> lats = new LinkedList<>();
                List<Double> longs = new LinkedList<>();
                List<Float> radii = new LinkedList<>();
                lats.add(47.625);
                longs.add(-122.25);
                radii.add(10000f);
                String message = "danger";
                new SubmitNarrowcastMessageTask(getActivity(), view, lats,longs,radii,message).execute();
            }
        });

        b2b.setText("");
        b2b.setVisibility(View.GONE);
        b5b.setText("");
        b5b.setVisibility(View.GONE);
    }
}
