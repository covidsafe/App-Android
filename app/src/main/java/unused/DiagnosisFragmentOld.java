package unused;

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

import edu.uw.covidsafe.comms.SendInfectedUserData;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import com.example.covidsafe.R;

import edu.uw.covidsafe.utils.CryptoUtils;
import edu.uw.covidsafe.utils.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DiagnosisFragmentOld extends Fragment {

    Button submitButton;
    TextInputEditText firstNameSubmit;
    TextInputEditText lastNameSubmit;
    EditText dobSubmit;
    CheckBox certBox;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        Log.e("logme","WARNING");
        View view = inflater.inflate(R.layout.health_diagnosis_old, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.health_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","diagnosis fragment onresume");
        Constants.DiagnosisFragment = this;
        Constants.CurrentFragment = this;

        submitButton = (Button)getActivity().findViewById(R.id.submitDiagnosisButton);
        firstNameSubmit = (TextInputEditText) getActivity().findViewById(R.id.firstNameSubmit);
        lastNameSubmit = (TextInputEditText) getActivity().findViewById(R.id.lastNameSubmit);
        dobSubmit = (EditText) getActivity().findViewById(R.id.dobSubmit);
        certBox = (CheckBox) getActivity().findViewById(R.id.certBoxWarning);
        updateUI();

        certBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                validateForm();
            }
        });

        firstNameSubmit.addTextChangedListener(textWatcher);
        lastNameSubmit.addTextChangedListener(textWatcher);
        dobSubmit.addTextChangedListener(textWatcher);

        submitButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitDiagnosisForm();
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
        String fname = firstNameSubmit.getText().toString().trim();
        String lname = lastNameSubmit.getText().toString().trim();
        String dob = dobSubmit.getText().toString().trim();

        if (!fname.isEmpty()&&!lname.isEmpty()&&!dob.isEmpty() && certBox.isChecked()) {
            submitButton.setEnabled(true);
        }
        else {
            submitButton.setEnabled(false);
        }
    }

    public void submitDiagnosisForm() {
        Utils.markDiagnosisSubmitted(getActivity());
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

//        new SendInfectedUserData(getContext(),
//                firstNameSubmit.getText().toString(),
//                lastNameSubmit.getText().toString(), dob).execute();

        AlertDialog dialog = new MaterialAlertDialogBuilder(getActivity())
                .setTitle("Thank you")
                .setMessage("")
                .setPositiveButton(R.string.ok,null)
                .setCancelable(false).create();
        dialog.show();
        submitButton.setEnabled(false);

        updateUI();

        CryptoUtils.generateInitSeed(getContext(), true);
    }

    public void updateUI() {
        if (Constants.statusSubmitted == -1) {
            boolean status = Utils.isDiagnosisSubmitted(getActivity());
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

        Constants.DiagnosisFragment = this;
        Constants.CurrentFragment = this;
    }
}
