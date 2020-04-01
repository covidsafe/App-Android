package edu.uw.covidsafe.ui.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.ui.MainActivity;
import com.example.covidsafe.R;

public class SettingsFragment extends Fragment {

    Button addButton;
    EditText addressText;
    TextView settingsHelperText;
    View view;
    RecyclerView rv;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("logme","HELP");
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getActivity().getString(R.string.settings_header_text));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.SettingsFragment = this;
        Constants.CurrentFragment = this;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SettingsFragment = this;
        Constants.CurrentFragment = this;
    }
}
