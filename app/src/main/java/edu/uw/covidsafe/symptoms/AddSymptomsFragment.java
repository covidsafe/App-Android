package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.util.Date;
import java.util.List;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.onboarding.OnboardingActivity;
import edu.uw.covidsafe.utils.Constants;

public class AddSymptomsFragment extends Fragment {

    Date date;
    String ampm;

    public AddSymptomsFragment(Date date, String ampm) {
        this.date = date;
        this.ampm = ampm;
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

        final Drawable upArrow = getActivity().getDrawable(R.drawable.abc_ic_ab_back_material);
        upArrow.setColorFilter(getActivity().getResources().getColor(R.color.black), PorterDuff.Mode.SRC_ATOP);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(upArrow);

        RecyclerView rview = view.findViewById(R.id.recyclerViewSymptomBoxes);
        SymptomRecyclerViewAdapter symptomAdapter = new SymptomRecyclerViewAdapter(getActivity(),getActivity());
        rview.setAdapter(symptomAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
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
