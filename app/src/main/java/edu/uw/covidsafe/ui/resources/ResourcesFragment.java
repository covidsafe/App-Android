package edu.uw.covidsafe.ui.resources;

import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ResourcesFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        View view = inflater.inflate(R.layout.fragment_resources, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.help_header_text));

        ExpandableListView lv = view.findViewById(R.id.faq);

        List<String> questions = new ArrayList<>();
        List<String> answers = new ArrayList<>();
        questions.add(getString(R.string.lipsum5));
        answers.add(getString(R.string.lipsum4));
        questions.add(getString(R.string.lipsum5));
        answers.add(getString(R.string.lipsum4));
        questions.add(getString(R.string.lipsum5));
        answers.add(getString(R.string.lipsum4));

        FaqListAdapter adapter = new FaqListAdapter(questions, answers);
        lv.setAdapter(adapter);

        //////////////////////////////////////////////////////////////////

        ExpandableListView lv2 = view.findViewById(R.id.faq2);

        List<String> questions2 = new ArrayList<>();
        List<String> answers2 = new ArrayList<>();
        questions2.add(getString(R.string.lipsum5));
        answers2.add(getString(R.string.lipsum4));
        questions2.add(getString(R.string.lipsum5));
        answers2.add(getString(R.string.lipsum4));
        questions2.add(getString(R.string.lipsum5));
        answers2.add(getString(R.string.lipsum4));

        FaqListAdapter adapter2 = new FaqListAdapter(questions2, answers2);
        lv2.setAdapter(adapter2);

        MaterialCardView res1 = view.findViewById(R.id.cdcView);
        res1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.goToUrl(getActivity(), "https://www.cdc.gov/");
            }
        });
        MaterialCardView res2 = view.findViewById(R.id.nycView);
        res2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.goToUrl(getActivity(), "https://www1.nyc.gov/site/doh/index.page");
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("logme","HELP");
        Constants.HelpFragment = this;
        Constants.CurrentFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.HelpFragment = this;
        Constants.CurrentFragment = this;
    }
}
