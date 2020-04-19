package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.onboarding.OnboardingActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class AddEditSymptomsFragment extends Fragment {

    Date date;
    String ampm;

    SymptomsRecord record;

    SymptomRecyclerViewAdapter symptomAdapter;

    String op;

    // add path
    public AddEditSymptomsFragment(Date date, String ampm) {
        Log.e("symptoms","add path");
        this.date = date;
        this.ampm = ampm;
        this.op = "add";
    }

    // edit path
    public AddEditSymptomsFragment(SymptomsRecord record) {
        Log.e("symptoms","edit path");
        this.record = record;
        this.op = "edit";
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("health","symptom tracker fragment oncreate");
        View view = inflater.inflate(R.layout.add_symptoms_fragment, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.add_symptoms_header_text)));

        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(getActivity().getDrawable(R.drawable.ic_close_black_24dp));

        RecyclerView rview = view.findViewById(R.id.recyclerViewSymptomBoxes);
        symptomAdapter = new SymptomRecyclerViewAdapter(getActivity(),getActivity(), op);
        rview.setAdapter(symptomAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (this.record != null) {
            Log.e("symptoms","update state");
            updateState();
        }

        Button submitButton = (Button)view.findViewById(R.id.submitForm);
        Button submitClear = (Button) view.findViewById(R.id.submitClear);
        CheckBox cb = (CheckBox)view.findViewById(R.id.certBoxReport);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!cb.isChecked()) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("Please confirm this information is accurate")
                            .setNegativeButton("Cancel",null)
                            .setPositiveButton("Ok",null)
                            .setCancelable(true).create();
                    dialog.show();
                }
                else {
                    // edit and replace
                    if (record != null) {
                        Utils.mkSnack(getActivity(), view, "Symptom log updated");
                        FragmentTransaction tx = ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                        tx.setCustomAnimations(
                                R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                        SymptomsRecord newRecord = new SymptomsRecord(
                                record.getTs(),
                                symptomAdapter.statesOut.get(0),
                                symptomAdapter.statesOut.get(1),
                                symptomAdapter.statesOut.get(2),
                                symptomAdapter.statesOut.get(3),
                                symptomAdapter.statesOut.get(4),
                                symptomAdapter.statesOut.get(5),
                                symptomAdapter.statesOut.get(6)
                        );
                        new SymptomsOpsAsyncTask(getContext(), newRecord).execute();
                        tx.replace(R.id.fragment_container, new SymptomConfirmFragment(newRecord)).commit();
                    }
                    else {
                        long ts = date.getTime();
                        if (ampm.equals("am")) {
                            ts += 3600000 * 9;
                        }
                        else if (ampm.equals("pm")) {
                            ts += 3600000 * 21;
                        }
                        SymptomsRecord newRecord = new SymptomsRecord(
                                ts,
                                symptomAdapter.statesOut.get(0),
                                symptomAdapter.statesOut.get(1),
                                symptomAdapter.statesOut.get(2),
                                symptomAdapter.statesOut.get(3),
                                symptomAdapter.statesOut.get(4),
                                symptomAdapter.statesOut.get(5),
                                symptomAdapter.statesOut.get(6)
                        );
                        new SymptomsOpsAsyncTask(getContext(), newRecord).execute();
                        Utils.mkSnack(getActivity(), view, "Symptom log updated");
                        FragmentTransaction tx = ((MainActivity) getActivity()).getSupportFragmentManager().beginTransaction();
                        tx.setCustomAnimations(
                                R.anim.enter_right_to_left, R.anim.exit_right_to_left,
                                R.anim.enter_left_to_right, R.anim.exit_left_to_right);
                        tx.replace(R.id.fragment_container, new SymptomConfirmFragment(newRecord)).commit();
                    }

                }
            }
        });
        submitClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Boolean> states = new LinkedList<>();
                for (int i = 0; i < symptomAdapter.symptoms.size(); i++) {
                    states.add(false);
                }
                symptomAdapter.updateContent(states);
            }
        });

        return view;
    }

    public void updateState() {
        List<Boolean> states = new LinkedList<>();
        states.add(record.getFever());
        states.add(record.getCough());
        states.add(record.getShortnessOfBreath());
        states.add(record.getTroubleBreathing());
        states.add(record.getChestPain());
        states.add(record.getConfusion());
        states.add(record.getBlue());
        symptomAdapter.updateContent(states);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("health","symptom tracker fragment onresume");

//        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
        Constants.CurrentFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
    }
}
