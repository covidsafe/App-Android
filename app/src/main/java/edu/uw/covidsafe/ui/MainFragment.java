package edu.uw.covidsafe.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import edu.uw.covidsafe.ui.health.CardRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class MainFragment extends Fragment {

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().hide();
        view = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView rview = view.findViewById(R.id.recyclerViewDiagnosis);
        CardRecyclerViewAdapter adapter = new CardRecyclerViewAdapter(getActivity(), getActivity());
        rview.setAdapter(adapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        ImageView settings = (ImageView) view.findViewById(R.id.settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, Constants.SettingsFragment).commit();
            }
        });

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
}
