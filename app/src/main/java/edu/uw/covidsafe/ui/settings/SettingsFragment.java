package edu.uw.covidsafe.ui.settings;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.ui.notif.NotifRecyclerViewAdapter;
import edu.uw.covidsafe.ui.onboarding.OnboardingActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Utils;

import com.example.covidsafe.R;

public class SettingsFragment extends Fragment {

    Button addButton;
    EditText addressText;
    TextView settingsHelperText;
    View view;
    RecyclerView rv;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Settings");

        RecyclerView rview3 = view.findViewById(R.id.recyclerViewPerms);
        PermissionsRecyclerViewAdapter adapter3 = new PermissionsRecyclerViewAdapter(getContext(),getActivity(), view);
        rview3.setAdapter(adapter3);
        rview3.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rview4 = view.findViewById(R.id.recyclerViewMore);
        MoreRecyclerViewAdapter adapter4 = new MoreRecyclerViewAdapter(getContext(),getActivity());
        rview4.setAdapter(adapter4);
        rview4.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rview5 = view.findViewById(R.id.recyclerViewMisc);
        MiscRecyclerViewAdapter adapter5 = new MiscRecyclerViewAdapter(getContext(),getActivity());
        rview5.setAdapter(adapter5);
        rview5.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.SettingsFragment = this;
        Constants.CurrentFragment = this;
        Constants.MainFragmentState = this;

        Log.e("perms","should update switch states? "+Constants.SuppressSwitchStateCheck);
        if (Constants.SuppressSwitchStateCheck) {
            Constants.SuppressSwitchStateCheck = false;
        }
        else {
            Utils.updateSwitchStates(getActivity());
        }
    }
}
