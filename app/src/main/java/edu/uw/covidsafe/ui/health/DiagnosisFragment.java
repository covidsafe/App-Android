package edu.uw.covidsafe.ui.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Date;

import edu.uw.covidsafe.comms.NetworkHelper;
import edu.uw.covidsafe.comms.SendInfectedUserData;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class DiagnosisFragment extends Fragment {

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_diagnosis, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Report Summary");

//        RecyclerView rview = view.findViewById(R.id.recyclerViewTips);
//        rview.setAdapter(Constants.TipAdapter);

        Button uploadButton = (Button)view.findViewById(R.id.uploadButton);
        if (uploadButton != null) {
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!NetworkHelper.isNetworkAvailable(getActivity())) {
                        Utils.mkSnack(getActivity(),view,"Network not available. Please try again.");
                    }
                    else {
                        new SendInfectedUserData(getContext(), getActivity(), view).execute();
                    }
                }
            });
        }

        Button whatHappens = (Button)view.findViewById(R.id.whatHappens);
        if (whatHappens != null) {
            whatHappens.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                            .setMessage("If you trace data, people who have visited any locations you've recently been to will be notified that they might have been exposed.")
                            .setPositiveButton("Dismiss", null)
                            .setCancelable(false).create();
                    dialog.show();
                }
            });
        }

        RecyclerView rview2 = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter adapter2 = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        rview2.setAdapter(adapter2);
        rview2.setLayoutManager(new LinearLayoutManager(getActivity()));

        SharedPreferences prefs = getActivity().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        long lastSubmissionDate = prefs.getLong(getString(com.example.covidsafe.R.string.last_submission_date_pkey), 0);

        // TODO: change back after demo
        TextView sick = (TextView)view.findViewById(R.id.sick);
        if (sick!=null) {
            sick.setText("");
            sick.setVisibility(View.GONE);
        }
        DiagnosisFragment.updateSubmissionView(getActivity(), getContext(), view, lastSubmissionDate, false);

        return view;
    }

    public static void updateSubmissionView(Activity av, Context context, View view, long lastSubmissionDate, boolean justReported) {
        av.runOnUiThread(new Runnable() {
            public void run() {
                if (lastSubmissionDate != 0) {
                    TextView pos = view.findViewById(R.id.pos);
                    if (justReported) {
                        Log.e("state","VISIBLE");
                        pos.setText(context.getString(R.string.pos_text));
                        pos.setVisibility(View.VISIBLE);
                    }
                    else {
                        pos.setText("");
                        pos.setVisibility(View.GONE);
                    }

                    TextView date = view.findViewById(R.id.date);
                    date.setVisibility(View.VISIBLE);
                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/YYYY");
                    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mma");
                    Date ts = new Date(lastSubmissionDate);
                    Spannable out = (Spannable) Html.fromHtml("<b>Last reported</b>: "+dateFormat.format(ts) + " at "+timeFormat.format(ts));
                    date.setText(out);
                }
                else {
                    TextView pos = view.findViewById(R.id.pos);
                    pos.setText("");
                    pos.setVisibility(View.GONE);
                    TextView date = view.findViewById(R.id.date);
                    date.setText("");
                    date.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.DiagnosisFragment = this;
        Constants.ReportFragmentState = this;
    }
}
