package edu.uw.covidsafe.ui.settings;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        Constants.menu.findItem(R.id.mybutton).setVisible(false);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(getActivity().getDrawable(R.drawable.ic_close_black_24dp));

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.settings_header_text)));

        RecyclerView rviewPerms = view.findViewById(R.id.recyclerViewPerms);
        PermissionsRecyclerViewAdapter adapter3 = new PermissionsRecyclerViewAdapter(getContext(),getActivity(), view);
        rviewPerms.setAdapter(adapter3);
        rviewPerms.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rviewTracesettings = view.findViewById(R.id.recyclerViewTraceSettings);
        TraceSettingsRecyclerViewAdapter adapterTraceSettings = new TraceSettingsRecyclerViewAdapter(getContext(),getActivity(), view);
        rviewTracesettings.setAdapter(adapterTraceSettings);
        rviewTracesettings.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rviewMore = view.findViewById(R.id.recyclerViewMore);
        MoreRecyclerViewAdapter adapter4 = new MoreRecyclerViewAdapter(getContext(),getActivity());
        rviewMore.setAdapter(adapter4);
        rviewMore.setLayoutManager(new LinearLayoutManager(getActivity()));

        RecyclerView rviewMisc = view.findViewById(R.id.recyclerViewMisc);
        MiscRecyclerViewAdapter adapter5 = new MiscRecyclerViewAdapter(getContext(),getActivity());
        rviewMisc.setAdapter(adapter5);
        rviewMisc.setLayoutManager(new LinearLayoutManager(getActivity()));

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
