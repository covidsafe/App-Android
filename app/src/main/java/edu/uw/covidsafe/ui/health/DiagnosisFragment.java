package edu.uw.covidsafe.ui.health;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import edu.uw.covidsafe.comms.SendInfectedUserData;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class DiagnosisFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_diagnosis, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Report Summary");

        RecyclerView rview = view.findViewById(R.id.recyclerViewDiagnosis);
        CardRecyclerViewAdapter adapter = new CardRecyclerViewAdapter(getActivity(), getActivity());
        if (rview != null) {
            rview.setAdapter(adapter);
            rview.setLayoutManager(new LinearLayoutManager(getActivity()));
        }

        Button uploadButton = (Button)view.findViewById(R.id.uploadButton);
        if (uploadButton != null) {
            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new SendInfectedUserData(getContext(), getActivity(), view).execute();
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

        MaterialCardView res1 = view.findViewById(R.id.cdcView);
        if (res1 != null) {
            res1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.goToUrl(getActivity(), "https://www.cdc.gov/");
                }
            });
        }

        MaterialCardView res2 = view.findViewById(R.id.nycView);
        if (res2 != null) {
            res2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.goToUrl(getActivity(), "https://www1.nyc.gov/site/doh/index.page");
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.DiagnosisFragment = this;
        Constants.ReportFragmentState = this;
    }
}
