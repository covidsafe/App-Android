package edu.uw.covidsafe.contact_trace;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.covidsafe.R;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.health.HealthPageAdapter;
import edu.uw.covidsafe.utils.Constants;

public class ContactTraceFragment extends Fragment {

    View view;
    ContactPageAdapter adapter;

    @SuppressLint("RestrictedApi")
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.contact_trace_pager, container, false);
        Log.e("state","contact trace fragment");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((MainActivity) getActivity()).getSupportActionBar().setHomeAsUpIndicator(getActivity().getDrawable(R.drawable.ic_close_black_24dp));
        ((MainActivity) getActivity()).getSupportActionBar().show();

        String header_str = getActivity().getString(R.string.contact_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.contact_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        Constants.contactViewPager = view.findViewById(R.id.pager);
        adapter = new ContactPageAdapter(getChildFragmentManager(), getContext());
        Constants.contactViewPager.setAdapter(adapter);

        if (Constants.ContactPageNumber != -1) {
            Log.e("contactnum","page number "+Constants.ContactPageNumber);
            if (Constants.ContactPageNumber == 0) {
            Constants.contactViewPager.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Constants.contactViewPager.setCurrentItem(Constants.ContactPageNumber);
                }
            }, 100);
            }
            else {
                Constants.contactViewPager.setCurrentItem(Constants.ContactPageNumber);
            }
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(false);
        }
    }
}
