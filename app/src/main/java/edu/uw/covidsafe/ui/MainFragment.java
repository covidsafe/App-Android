package edu.uw.covidsafe.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ExpandableListAdapter;
import android.widget.ImageView;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidsafe.R;
import com.google.android.material.card.MaterialCardView;

import edu.uw.covidsafe.ui.health.CardRecyclerViewAdapter;
import edu.uw.covidsafe.ui.health.ResourceRecyclerViewAdapter;
import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class MainFragment extends Fragment {

    View view;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
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

        RecyclerView rview2 = view.findViewById(R.id.recyclerViewResources);
        ResourceRecyclerViewAdapter adapter2 = new ResourceRecyclerViewAdapter(getContext(),getActivity());
        rview2.setAdapter(adapter2);
        rview2.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rview3 = view.findViewById(R.id.recyclerViewNotifs);
        NotifRecyclerViewAdapter adapter3 = new NotifRecyclerViewAdapter(getContext(),getActivity());
        rview3.setAdapter(adapter3);
        rview3.setLayoutManager(new LinearLayoutManager(getActivity()));

        Switch bb = view.findViewById(R.id.switch1);
        bb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    adapter3.notifyUser();
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.MainFragment = this;
    }
}
