package edu.uw.covidsafe.ui.health;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.covidsafe.R;
import edu.uw.covidsafe.ui.MainActivity;
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

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.health_header_text)));

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, Constants.SymptomTrackerFragment).commit();
                        break;
                    case 1:
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health, Constants.DiagnosisFragment).commit();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        MaterialCalendarView cal = view.findViewById(R.id.calendarView);

        /////////////////////////////////////////////////////////////////
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, -Constants.DefaultInfectionWindowInDays);
        /////////////////////////////////////////////////////////////////
        Log.e("health","minimum "+calendar.get(Calendar.YEAR)+","+(calendar.get(Calendar.MONTH)+1)+","+calendar.get(Calendar.DAY_OF_MONTH));
        CalendarDay d1 = CalendarDay.from(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH)+1,calendar.get(Calendar.DAY_OF_MONTH));
        cal.setSelectedDate(CalendarDay.today());

        cal.state().edit()
                .setFirstDayOfWeek(DayOfWeek.SUNDAY)
                .setMinimumDate(d1)
                .setMaximumDate(CalendarDay.today())
                .setCalendarDisplayMode(CalendarMode.WEEKS)
                .commit();
        cal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                Log.e("health",date.toString());

                //find the date
//                Constants.symptomRecords =
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Constants.HealthFragment = this;

        Log.e("health","onresume "+Constants.HealthFragmentState.toString());
        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        if (Constants.HealthFragmentState.toString().toLowerCase().contains("diagnosis")){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health,
                    Constants.DiagnosisFragment).commit();
            tabLayout.getTabAt(1).select();
            Log.e("health","select 1");
        }
        else if (Constants.HealthFragmentState.toString().toLowerCase().contains("symptom")) {
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_health,
                    Constants.SymptomTrackerFragment).commit();
            tabLayout.getTabAt(0).select();
            Log.e("health","select 0");
        }
    }
}
