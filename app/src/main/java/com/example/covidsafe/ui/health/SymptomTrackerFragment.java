package com.example.covidsafe.ui.health;

import android.os.Bundle;
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

import com.example.covidsafe.comms.NetworkHelper;
import com.example.covidsafe.symptoms.SymptomsOpsAsyncTask;
import com.example.covidsafe.symptoms.SymptomsRecord;
import com.example.covidsafe.ui.MainActivity;
import com.example.covidsafe.R;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.FileOperations;
import com.example.covidsafe.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SymptomTrackerFragment extends Fragment {

    CheckBox cb1;
    CheckBox cb2;
    CheckBox cb3;
    CheckBox cb4;
    CheckBox cb5;
    CheckBox cb6;
    CheckBox cb7;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_symptom_tracker, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.health_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","symptom tracker fragment onresume");

        Constants.CurrentFragment = this;
        Constants.SymptomTrackerFragment = this;

//        TextView lastSubmitted = (TextView) getActivity().findViewById(R.id.lastSubmittedTv);

        cb1 = (CheckBox) getActivity().findViewById(R.id.symptom1);
        cb2 = (CheckBox) getActivity().findViewById(R.id.symptom2);
        cb3 = (CheckBox) getActivity().findViewById(R.id.symptom3);
        cb4 = (CheckBox) getActivity().findViewById(R.id.symptom4);
        cb5 = (CheckBox) getActivity().findViewById(R.id.symptom5);
        cb6 = (CheckBox) getActivity().findViewById(R.id.symptom6);
        cb7 = (CheckBox) getActivity().findViewById(R.id.symptom7);

        final CheckBox certBox = (CheckBox) getActivity().findViewById(R.id.certBoxReport);

        cb1.setText(getString(R.string.symptom1));
        cb2.setText(getString(R.string.symptom2));
        cb3.setText(getString(R.string.symptom3));
        cb4.setText(getString(R.string.symptom4));
        cb5.setText(getString(R.string.symptom5));
        cb6.setText(getString(R.string.symptom6));
        cb7.setText(getString(R.string.symptom7));

        Button clearButton = (Button)getActivity().findViewById(R.id.submitClear);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cb1.setChecked(false);
                cb2.setChecked(false);
                cb3.setChecked(false);
                cb4.setChecked(false);
                cb5.setChecked(false);
                cb6.setChecked(false);
                cb7.setChecked(false);
                certBox.setChecked(false);
            }
        });

        final Button submitButton = (Button)getActivity().findViewById(R.id.submitForm);

        Date date = Utils.getLastSubmitTime(getActivity());
        if (date != null) {
            boolean compare = Utils.compareDates(date, Constants.SubmitThresh);
            submitButton.setEnabled(compare);
        }
        else {
            submitButton.setEnabled(true);
        }

        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm aa");
//            lastSubmitted.setText("Last submitted:\n"+dateFormat.format(date));
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!certBox.isChecked()) {
                    AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                            .setTitle("Error")
                            .setMessage(getActivity().getString(R.string.certError))
                            .setPositiveButton(R.string.ok,null)
                            .setCancelable(false).create();
                    dialog.show();
                }
                else {
                    submitForm();
                }
            }
        });
    }

    public void submitForm() {
        Date date = Utils.getLastSubmitTime(getActivity());
        if (date == null || Utils.compareDates(date, Constants.SubmitThresh)) {
            FileOperations.append(Utils.getFormRecordName(), getActivity(), Constants.formDirName, Constants.logFileName);
            AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                    .setTitle("Thank you")
                    .setPositiveButton(R.string.ok,null)
                    .setCancelable(false).create();
            dialog.show();
            SymptomsRecord rec = new SymptomsRecord(System.currentTimeMillis(),
                    cb1.isChecked(),cb2.isChecked(),cb3.isChecked(),cb4.isChecked(),
                    cb5.isChecked(),cb6.isChecked(),cb7.isChecked());
            new SymptomsOpsAsyncTask(getContext(), rec).execute();
            NetworkHelper.sendRecords(rec.toJson(), getContext());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SymptomTrackerFragment = this;
        Constants.CurrentFragment = this;
    }
}
