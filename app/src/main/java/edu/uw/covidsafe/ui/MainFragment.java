package edu.uw.covidsafe.ui;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
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
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.comms.PullFromServerTask;
import edu.uw.covidsafe.comms.PullFromServerTaskDemo;
import edu.uw.covidsafe.comms.PullFromServerTaskDemo2;
import edu.uw.covidsafe.symptoms.SymptomDbModel;
import edu.uw.covidsafe.symptoms.SymptomUtils;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifDbModel;
import edu.uw.covidsafe.ui.notif.NotifOpsAsyncTask;
import edu.uw.covidsafe.ui.notif.NotifRecord;
import edu.uw.covidsafe.ui.settings.PermUtils;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;
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
    boolean symptomDbChanged = false;
    List<SymptomsRecord> changedRecords;

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
        tipView.setAdapter(Constants.MainTipAdapter);
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

        ImageView xall = (ImageView) view.findViewById(R.id.xall);
        if (Constants.DEBUG) {
            xall.setAlpha(1f);
        }
        else {
            xall.setAlpha(0f);
        }
        xall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new NotifOpsAsyncTask(getContext(),Constants.NotifDatabaseOps.DeleteAll).execute();
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
//                Constants.HistoryAdapter.setRecords(new LinkedList<>(), view);
//                Constants.NotificationAdapter.setRecords(currentNotifs, view);
//                Constants.MainTipAdapter.enableTips(1, view);

                Constants.HistoryAdapter.setRecords(historyNotifs, view);
                Constants.NotificationAdapter.setRecords(currentNotifs, view);
                Constants.MainTipAdapter.enableTips(notifRecords.size(), view, false);
            }
        });

        SymptomDbModel smodel = ViewModelProviders.of(getActivity()).get(SymptomDbModel.class);
        smodel.getAllSorted().observe(getActivity(), new Observer<List<SymptomsRecord>>() {
            @Override
            public void onChanged(List<SymptomsRecord> symptomRecords) {
                //something in db has changed, update
                symptomDbChanged = true;
                changedRecords = symptomRecords;
                Constants.symptomRecords = symptomRecords;
                Log.e("symptom","mainfragment - symptom list changed");
                if (Constants.CurrentFragment.toString().toLowerCase().contains("mainfragment")) {
                    Log.e("symptom","mainfragment - symptom list changing");
                    SymptomUtils.updateTodaysLogs(view, symptomRecords, getContext(), getActivity(),
                            new Date(TimeUtils.getTime()), "main");
                    symptomDbChanged = false;
                }
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

        initDataStorageLengthUI();

        return view;
    }

    public void initDataStorageLengthUI() {
        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        TextView localDataStorageText = (TextView)view.findViewById(R.id.localDataStorageText);
        int currentDaysOfDataToKeep = 0;
        if (Constants.DEBUG) {
            currentDaysOfDataToKeep = prefs.getInt(getActivity().getString(R.string.infection_window_in_days_pkeys), Constants.DefaultDaysOfLogsToKeepDebug);
        }
        else {
            currentDaysOfDataToKeep = prefs.getInt(getActivity().getString(R.string.infection_window_in_days_pkeys), Constants.DefaultDaysOfLogsToKeep);
        }

        Date dd = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        calendar.add(Calendar.DATE, currentDaysOfDataToKeep);
        long thresh = calendar.getTime().getTime();

        SimpleDateFormat format = new SimpleDateFormat("MMMM d");
        String ss = format.format(new Date(thresh));

        String out = "Your data will expire on "+ss+".\n\nOn this date your symptom logs and location data will be removed from the app. This action cannot be undone.\n";

        localDataStorageText.setText(out);

        TextView changeInSettings = (TextView) view.findViewById(R.id.changeInSettings);
        changeInSettings.setText((Spannable)Html.fromHtml("<u>Change in settings</u>"));
        changeInSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("mainfragment","onclick");
                FragmentTransaction tx = getActivity().getSupportFragmentManager().beginTransaction();
                tx.setCustomAnimations(
                        R.anim.enter_right_to_left,R.anim.exit_right_to_left,
                        R.anim.enter_left_to_right,R.anim.exit_left_to_right);
                tx.replace(R.id.fragment_container, Constants.SettingsFragment).commit();
            }
        });
    }

    public void refreshTask() {
        Log.e("refresh","freshtask ");
        if (!Constants.PullFromServerTaskRunning) {
            if (!Constants.PUBLIC_DEMO) {
                if (Constants.DEBUG) {
                    new PullFromServerTaskDemo(getContext(), getActivity(), view).execute();
                } else {
                    new PullFromServerTask(getContext(), getActivity(), view).execute();
                }

                RotateAnimation rotate = new RotateAnimation(0,360,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                rotate.setDuration(1000);
                rotate.setRepeatCount(Animation.INFINITE);
                rotate.setRepeatMode(Animation.INFINITE);
                rotate.setInterpolator(new LinearInterpolator());
                refresh.startAnimation(rotate);
            }
            else {
                swipeLayout.setRefreshing(false);
                refresh.clearAnimation();
            }
        }
    }

    public void broadcastSwitchLogic(boolean isChecked) {
        Log.e("state","broadcast switch logic");
        if (isChecked) {
            PermUtils.gpsSwitchLogic(getActivity());
            PermUtils.bleSwitchLogic(getActivity());
        }
        else {
            Utils.haltLoggingService(getActivity(), null);
            PermUtils.transition(true, getActivity());
        }

        updateBroadcastUI(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","main fragment on resume "+Constants.PullServiceRunning+","+Constants.LoggingServiceRunning);
        Constants.CurrentFragment = this;
        Constants.MainFragment = this;
        Constants.MainFragmentState = this;

        updateBroadcastUI(false);

        swipeLayout.setRefreshing(false);
        refresh.clearAnimation();

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        long ts = prefs.getLong(getActivity().getString(R.string.last_refresh_date_pkey), 0);
        if (ts != 0) {
            SimpleDateFormat format = new SimpleDateFormat("h:mm a");
            lastUpdated.setText("Last updated: "+format.format(new Date(ts)));
        }
        else {
            lastUpdated.setText("");
            lastUpdated.setVisibility(View.GONE);
        }

        if (symptomDbChanged) {
            Log.e("symptoms","db changed ");
            SymptomUtils.updateTodaysLogs(view, changedRecords, getContext(),
                    getActivity(), new Date(TimeUtils.getTime()), "main");
            symptomDbChanged = false;
        }
    }

    public void updateBroadcastUI(boolean animate) {
        Log.e("state","update broadcast ui");
        boolean hasGpsPerms = Utils.hasGpsPermissions(getActivity());
        boolean hasBlePerms = Utils.hasBlePermissions(getActivity());

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean gpsEnabled = prefs.getBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
        boolean bleEnabled = prefs.getBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);

        if (!hasGpsPerms) {
            Log.e("state","no gps");
            editor.putBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
            editor.commit();
        }
        if (!hasBlePerms) {
            Log.e("state","no ble");
            editor.putBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
            editor.commit();
        }

        if ((!hasGpsPerms && !hasBlePerms) || (!gpsEnabled && !bleEnabled)) {
            Log.e("state","no perms");
            editor.putBoolean(getActivity().getString(R.string.gps_enabled_pkey), false);
            editor.putBoolean(getActivity().getString(R.string.ble_enabled_pkey), false);
            editor.commit();

            if (animate) {
                Log.e("transition","set to off");
                broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_off));
                broadcastRing.setAlpha(0f);

                PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f,0f);
                PropertyValuesHolder a2 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f,1f);
                ObjectAnimator anim1 = ObjectAnimator.ofPropertyValuesHolder(broadcastTitle, a1);
                anim1.setDuration(1000);
                ObjectAnimator anim2 = ObjectAnimator.ofPropertyValuesHolder(broadcastTitle, a2);
                anim2.setDuration(1000);
                anim1.start();
                broadcastTitle.setText("Broadcasting Off");
                anim2.start();

                a1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f,0f);
                a2 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f,1f);
                anim1 = ObjectAnimator.ofPropertyValuesHolder(broadcastProp, a1);
                anim1.setDuration(1000);
                anim2 = ObjectAnimator.ofPropertyValuesHolder(broadcastProp, a2);
                anim2.setDuration(1000);
                anim1.start();
                Utils.linkify(broadcastProp,getString(R.string.stopping));
                anim2.start();
            }
            else {
                broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_off));
                broadcastRing.setAlpha(0f);
                broadcastTitle.setText("Broadcasting Off");
                Utils.linkify(broadcastProp,getString(R.string.stopping));
            }
        }
        else if (gpsEnabled || bleEnabled) {
            Log.e("state","has one enabled");
            if (animate) {
                Log.e("transition","set to on");
                broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_on));
                broadcastRing.setAlpha(1f);

                PropertyValuesHolder a1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f,0f);
                PropertyValuesHolder a2 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f,1f);
                ObjectAnimator anim1 = ObjectAnimator.ofPropertyValuesHolder(broadcastTitle, a1);
                anim1.setDuration(1000);
                ObjectAnimator anim2 = ObjectAnimator.ofPropertyValuesHolder(broadcastTitle, a2);
                anim2.setDuration(1000);
                anim1.start();
                broadcastTitle.setText("Broadcasting On");
                anim2.start();

                a1 = PropertyValuesHolder.ofFloat(View.ALPHA, 1f,0f);
                a2 = PropertyValuesHolder.ofFloat(View.ALPHA, 0f,1f);
                anim1 = ObjectAnimator.ofPropertyValuesHolder(broadcastProp, a1);
                anim1.setDuration(1000);
                anim2 = ObjectAnimator.ofPropertyValuesHolder(broadcastProp, a2);
                anim2.setDuration(1000);
                anim1.start();
                Utils.linkify(broadcastProp,getString(R.string.logging));
                anim2.start();
            }
            else {
                broadcastSwitch.setImageDrawable(getActivity().getDrawable(R.drawable.switch_on));
                broadcastRing.setAlpha(1f);
                broadcastTitle.setText("Broadcasting On");
                Utils.linkify(broadcastProp,getString(R.string.logging));
            }
        }
    }


    public void initTestButtons() {
//        Button b2b = (Button)view.findViewById(R.id.button9);
//        b2b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
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

//            }
//        });

//        Button b5b = (Button)view.findViewById(R.id.button10);
//        b5b.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                String ss = model.records.getValue().size()+"";
//                for (int i = 0; i < model.records.getValue().size(); i++) {
//                    ss+=model.records.getValue().get(i).current;
//                }
//                Toast.makeText(getContext(), "notif size "+ss,Toast.LENGTH_LONG).show();

//                List<Double> lats = new LinkedList<>();
//                List<Double> longs = new LinkedList<>();
//                List<Float> radii = new LinkedList<>();
//                lats.add(47.625);
//                longs.add(-122.25);
//                radii.add(10000f);
//                String message = "danger";
//                new SubmitNarrowcastMessageTask(getActivity(), view, lats,longs,radii,message).execute();
//            }
//        });

//        b2b.setText("");
//        b2b.setVisibility(View.GONE);
//        b5b.setText("");
//        b5b.setVisibility(View.GONE);
    }
}
