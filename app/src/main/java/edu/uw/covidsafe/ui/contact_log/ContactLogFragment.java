package edu.uw.covidsafe.ui.contact_log;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.R;
import com.google.android.material.tabs.TabLayout;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class ContactLogFragment extends Fragment {

    static View view;
    ContactLogPageAdapter adapter;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contact_log, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        String header_str = getActivity().getString(R.string.contact_log_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.contact_log_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        Constants.contactLogViewPager = view.findViewById(R.id.pager);
        adapter = new ContactLogPageAdapter(getChildFragmentManager());
        Constants.contactLogViewPager.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(Constants.contactLogViewPager);

        Constants.contactLogCal = view.findViewById(R.id.calendarView);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
//        Constants.ContactLogFragment = this;
    }
}
