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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;

import java.text.SimpleDateFormat;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class SymptomConfirmFragment extends Fragment {

    View view;
    SymptomsRecord record;

    public SymptomConfirmFragment(SymptomsRecord record) {
        this.record = record;
    }

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("health", "symptom tracker fragment oncreate");
        view = inflater.inflate(R.layout.fragment_symptom_confirmation, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.add_symptoms_header_text)));

        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(getActivity().getDrawable(R.drawable.ic_close_black_24dp));


        RecyclerView tipView = view.findViewById(R.id.recyclerViewSymptomConfirm);
        SymptomSummaryRecyclerViewAdapter symptomAdapter = new SymptomSummaryRecyclerViewAdapter(getActivity(),getActivity(), view, record);
        tipView.setAdapter(symptomAdapter);
        tipView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    public void format() {
        SimpleDateFormat format = new SimpleDateFormat("aa");
        TextView ampm = (TextView) view.findViewById(R.id.ampm);
        ampm.setText(format.format(this.record.getTs()));

        TextView dateDetails = (TextView) view.findViewById(R.id.dateDetails);
        SimpleDateFormat format2 = new SimpleDateFormat("MMMM dd, yyyy");
        SimpleDateFormat format3 = new SimpleDateFormat("hh:mm aa");

        dateDetails.setText(format2.format(this.record.getLogTime()) + "|" +
        format3.format(this.record.getLogTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("health","symptom tracker fragment onresume");

        Constants.CurrentFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.CurrentFragment = this;
    }
}
