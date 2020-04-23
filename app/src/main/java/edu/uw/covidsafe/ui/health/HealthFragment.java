package edu.uw.covidsafe.ui.health;

import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.covidsafe.R;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.ui.onboarding.OnboardingStateAdapter;
import edu.uw.covidsafe.utils.Constants;
import com.google.android.material.tabs.TabLayout;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;

import java.util.Calendar;
import java.util.Date;

public class HealthFragment extends Fragment {

    View view;
    HealthPageAdapter adapter;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.health_main, container, false);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getActivity().getResources().getColor(R.color.white));
        }

//        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
//            Constants.menu.findItem(R.id.mybutton).setVisible(true);
//        }
        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();

        String header_str = getActivity().getString(R.string.health_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.health_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        Constants.healthViewPager = view.findViewById(R.id.pager);
        adapter = new HealthPageAdapter(getChildFragmentManager());
        Constants.healthViewPager.setAdapter(adapter);

        Constants.healthViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                Log.e("time","onpagescrolled "+position);
                if (position == 0) {
                    if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
                        Constants.menu.findItem(R.id.mybutton).setVisible(true);
                    }
                }
                else {
                    if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
                        Constants.menu.findItem(R.id.mybutton).setVisible(false);
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
//                Log.e("time","onpageselected "+position);
                if (position == 0) {
                    if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
                        Constants.menu.findItem(R.id.mybutton).setVisible(true);
                    }
                }
                else {
                    if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
                        Constants.menu.findItem(R.id.mybutton).setVisible(false);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.e("time","scrollstatechanged "+state);
            }
        });
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(Constants.healthViewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.HealthFragment = this;
        Constants.CurrentFragment = this;

        Log.e("health","onresume "+Constants.HealthFragmentState.toString());
    }
}
