package com.example.corona.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.corona.utils.Constants;
import com.example.corona.utils.FileOperations;
import com.example.corona.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class WarningFragment extends Fragment {

    Button submitButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.e("logme","WARNING");
        View view = inflater.inflate(R.layout.fragment_warning, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.warning_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.e("logme","WARNING");
        Constants.WarningFragment = this;
        Constants.CurrentFragment = this;

        submitButton = (Button)getActivity().findViewById(R.id.submitDiagnosisButton);
        final CheckBox certBox = (CheckBox) getActivity().findViewById(R.id.certBoxWarning);
        updateUI();

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
        FileOperations.markStatusSubmitted(getActivity());
        updateUI();
    }

    public void updateUI() {
        if (Constants.statusSubmitted == -1) {
            boolean status = FileOperations.reportStatusSubmitted(getActivity());
            Constants.statusSubmitted = status ? 1 : 0;
        }
        if (Constants.statusSubmitted == 1) {
            submitButton.setEnabled(false);
        }
        else {
            submitButton.setEnabled(true);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.WarningFragment = this;
        Constants.CurrentFragment = this;
    }
}
