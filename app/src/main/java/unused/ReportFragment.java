package unused;

import android.content.Intent;
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

import com.example.corona.Constants;
import com.example.corona.FileOperations;
import com.example.corona.MainActivity;
import com.example.corona.R;
import com.example.corona.Utils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.report_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView lastSubmitted = (TextView) getActivity().findViewById(R.id.lastSubmittedTv);

        CheckBox cb1 = (CheckBox) getActivity().findViewById(R.id.symptom1);
        CheckBox cb2 = (CheckBox) getActivity().findViewById(R.id.symptom2);
        CheckBox cb3 = (CheckBox) getActivity().findViewById(R.id.symptom3);
        CheckBox cb4 = (CheckBox) getActivity().findViewById(R.id.symptom4);
        CheckBox cb5 = (CheckBox) getActivity().findViewById(R.id.symptom5);
        CheckBox cb6 = (CheckBox) getActivity().findViewById(R.id.symptom6);
        CheckBox cb7 = (CheckBox) getActivity().findViewById(R.id.symptom7);

        final CheckBox certBox = (CheckBox) getActivity().findViewById(R.id.certBox);

        cb1.setText(getString(R.string.symptom1));
        cb2.setText(getString(R.string.symptom2));
        cb3.setText(getString(R.string.symptom3));
        cb4.setText(getString(R.string.symptom4));
        cb5.setText(getString(R.string.symptom5));
        cb6.setText(getString(R.string.symptom6));
        cb7.setText(getString(R.string.symptom7));

        final Button submitButton = (Button)getActivity().findViewById(R.id.submitForm);

        Date date = Utils.getLastSubmitTime(getActivity());
        if (date != null) {
            boolean compare = Utils.compareDates(date);
            submitButton.setEnabled(compare);
        }
        else {
            submitButton.setEnabled(true);
        }

        if (date != null) {
            DateFormat dateFormat = new SimpleDateFormat("MM-dd-yyyy hh:mm aa");
            lastSubmitted.setText("Last submitted:\n"+dateFormat.format(date));
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

        Constants.CurrentFragment = this;
        Constants.ReportFragment = this;
    }

    public void submitForm() {
        Date date = Utils.getLastSubmitTime(getActivity());
        if (date == null || Utils.compareDates(date)) {
            FileOperations.append(Utils.getFormRecordName(), getActivity(), Constants.formDirName, Constants.logFileName);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.ReportFragment = this;
        Constants.CurrentFragment = this;
    }
}
