package edu.uw.covidsafe.ui.health;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getActivity().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.health_header_text)));

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
                            .setMessage("If you upload  trace data, people who have visited any locations you've recently been to will be notified that they might have been exposed.")
                            .setNegativeButton("Learn more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.covidSiteLink)));
                                    startActivity(browserIntent);
                                }
                            })
                            .setPositiveButton("Dismiss", null)
                            .setCancelable(false).create();
                    dialog.show();
                    dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getContext().getResources().getColor(R.color.darkGray));
                }
            });
        }

        RecyclerView rview2 = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter adapter2 = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        rview2.setAdapter(adapter2);
        rview2.setLayoutManager(new LinearLayoutManager(getActivity()));

        DiagnosisFragment.updateSubmissionView(getActivity(), getContext(), view, false);

        return view;
    }

    public static void updateSubmissionView(Activity av, Context context, View view, boolean justReported) {
        av.runOnUiThread(new Runnable() {
            public void run() {
                TextView pos = view.findViewById(R.id.pos);
                TextView date = view.findViewById(R.id.date);
                if (justReported) {
                    Log.e("state","VISIBLE");
                    pos.setText(context.getString(R.string.pos_text));
                    pos.setVisibility(View.VISIBLE);
                    date.setText("");
                    date.setVisibility(View.GONE);
                }
                else {
                    pos.setText("");
                    pos.setVisibility(View.GONE);
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
