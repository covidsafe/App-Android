package com.example.covidsafe.ui.onboarding;

import android.os.Bundle;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.covidsafe.R;
import com.example.covidsafe.ui.MainActivity;
import com.example.covidsafe.utils.Constants;
import com.example.covidsafe.utils.Utils;

public class StartFragment extends Fragment {

    Button getStartedButton;
    TextView privacy;
    TextView terms;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.onboarding_start, container, false);
        ((OnboardingActivity) getActivity()).getSupportActionBar().hide();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("state","start fragment on resume");
        Constants.CurrentFragment = this;
        Constants.StartFragment = this;

        getStartedButton = (Button) getActivity().findViewById(R.id.onboardingGetStartedButton);
        getStartedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_onboarding, new PermissionFragment()).commit();
            }
        });

        privacy = (TextView) getActivity().findViewById(R.id.privacyTextView);
        terms = (TextView) getActivity().findViewById(R.id.termsTextView);
        Utils.linkify(privacy, getString(R.string.privacy_link));
        Utils.linkify(terms, getString(R.string.terms_link));
    }
}
