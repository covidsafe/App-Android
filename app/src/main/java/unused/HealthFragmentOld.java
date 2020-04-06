package unused;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.R;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import com.google.android.material.tabs.TabLayout;

public class HealthFragmentOld extends Fragment {

    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.health_main_old, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.health_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        tabLayout = getActivity().findViewById(R.id.tabLayout);
        Log.e("state","HEALTH FRAGMENT STATE "+Constants.CurrentFragment.toString());
        if (Constants.CurrentFragment.toString().toLowerCase().contains("symptom")) {
            Log.e("state","health fragment go to symptom");
            TabLayout.Tab tab = tabLayout.getTabAt(0);
            tab.select();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, Constants.SymptomTrackerFragment).commit();
        }
        else if (Constants.CurrentFragment.toString().toLowerCase().contains("diagnosis")) {
            Log.e("state","health fragment go to diagnosis");
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            tab.select();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, Constants.DiagnosisFragment).commit();
        }
        else {
            Constants.CurrentFragment = this;
            Constants.HealthFragment = this;

            Log.e("state", "health fragment go to symptom tracker");
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, new SymptomTrackerFragment()).commit();
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText().toString().toLowerCase().contains("symptom")) {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, new SymptomTrackerFragment()).commit();
                }
                else {
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, new DiagnosisFragmentOld()).commit();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
