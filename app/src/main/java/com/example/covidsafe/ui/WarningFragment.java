package com.example.covidsafe.ui;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.comms.SendInfectedLogsOfUser;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.FileOperations;
import com.example.covidsafe.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WarningFragment extends Fragment {

    Button submitButton;
    EditText nameSubmit;
    EditText dobSubmit;
    CheckBox certBox;
    DatePickerDialog.OnDateSetListener mDateSetListener;

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
        nameSubmit = (EditText) getActivity().findViewById(R.id.nameSubmit);
        dobSubmit = (EditText) getActivity().findViewById(R.id.dobSubmit);
        certBox = (CheckBox) getActivity().findViewById(R.id.certBoxWarning);
        updateUI();

        certBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validateForm();
            }
        });

        nameSubmit.addTextChangedListener(textWatcher);
        dobSubmit.addTextChangedListener(textWatcher);

        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitForm();
            }
        });

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                dialog = null;
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        dobSubmit.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ontouch",(dialog==null)+"");
                if (dialog == null) {

                    dialog = new DatePickerDialog(getContext(), date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));

                    dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface d, int which) {
                            if (which == DialogInterface.BUTTON_NEGATIVE) {
                                dialog = null;
                            }
                        }
                    });
                    dialog.show();
                }
                return false;
            }
        });
    }

    DatePickerDialog dialog;
    Calendar myCalendar = Calendar.getInstance();

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        dobSubmit.setText(sdf.format(myCalendar.getTime()));
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            validateForm();
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void validateForm() {
        String name = nameSubmit.getText().toString().trim();
        String dob = dobSubmit.getText().toString().trim();

        if (!name.isEmpty()&&!dob.isEmpty() && certBox.isChecked()) {
            submitButton.setEnabled(true);
        }
        else {
            submitButton.setEnabled(false);
        }
    }

    public void submitForm() {
        FileOperations.markStatusSubmitted(getActivity());
        String rawdob = dobSubmit.getText().toString();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        long dob = 0;
        try {
            Date dd = format.parse(rawdob);
            dob = dd.getTime();
        }
        catch(Exception e) {
            Log.e("ble",e.getMessage());
        }

        new SendInfectedLogsOfUser(getContext(), nameSubmit.getText().toString(), dob).execute();

        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Thank you")
                .setMessage("")
                .setPositiveButton(R.string.ok,null)
                .setCancelable(false).create();
        dialog.show();
        submitButton.setEnabled(false);

        updateUI();
    }

    public void updateUI() {
        if (Constants.statusSubmitted == -1) {
            boolean status = FileOperations.reportStatusSubmitted(getActivity());
            Constants.statusSubmitted = status ? 1 : 0;
        }
        if (Constants.statusSubmitted == 1 || Constants.DEBUG) {
            submitButton.setEnabled(false);
        }
        else {
            submitButton.setEnabled(false);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.WarningFragment = this;
        Constants.CurrentFragment = this;
    }
}
