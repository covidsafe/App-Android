package edu.uw.covidsafe.ui.health;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.R;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import com.google.android.material.tabs.TabLayout;

public class HealthFragment extends Fragment {

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_main, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.health_header_text));
        Button reportButton = (Button)view.findViewById(R.id.button3);
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.SubmitFragment).commit();
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.HealthFragment = this;
        Constants.ReportFragmentState = this;
    }
}
