package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

import com.example.covidsafe.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SymptomTrackerFragment extends Fragment {

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("health","symptom tracker fragment oncreate");
        View view = inflater.inflate(R.layout.health_symptom_tracker, container, false);

        ((TextView)view.findViewById(R.id.symptomTrackerTitle)).setText("Today");

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.health_header_text)));

        RecyclerView rview = view.findViewById(R.id.recyclerViewSymptomHistory);
        SymptomHistoryRecyclerViewAdapter symptomHistoryAdapter = new SymptomHistoryRecyclerViewAdapter(getActivity(),getActivity());
        rview.setAdapter(symptomHistoryAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        SymptomDbModel smodel = ViewModelProviders.of(getActivity()).get(SymptomDbModel.class);
        smodel.getAllSorted().observe(getActivity(), new Observer<List<SymptomsRecord>>() {
            @Override
            public void onChanged(List<SymptomsRecord> symptomRecords) {
                //something in db has changed, update
                Log.e("health","symptom list changed");
                Constants.symptomRecords = symptomRecords;
                SymptomUtils.updateTodaysLogs(view, symptomRecords, getContext(),getActivity());
                symptomHistoryAdapter.setRecords(symptomRecords, view);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("health","symptom tracker fragment onresume");

        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
    }
}
