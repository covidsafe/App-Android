package edu.uw.covidsafe.symptoms;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.TimeUtils;

import com.example.covidsafe.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.threeten.bp.DayOfWeek;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class SymptomTrackerFragment extends Fragment {

    static View view;
    static SymptomHistoryRecyclerViewAdapter symptomHistoryAdapter;
    boolean symptomDbChanged = false;
    static List<SymptomsRecord> changedRecords;
    List<CalendarDay> markedDays = new LinkedList<>();
    static MaterialCalendarView cal;

    @SuppressLint("RestrictedApi")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("health","symptom tracker fragment oncreate");
        view = inflater.inflate(R.layout.health_symptom_tracker, container, false);

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();

        String header_str = getActivity().getString(R.string.health_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.health_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        RecyclerView rview = view.findViewById(R.id.recyclerViewSymptomHistory);
        symptomHistoryAdapter = new SymptomHistoryRecyclerViewAdapter(getActivity(),getActivity(), view);
        rview.setAdapter(symptomHistoryAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));

        Log.e("symptom","init constants cal");
        initCal();

        SymptomDbModel smodel = ViewModelProviders.of(getActivity()).get(SymptomDbModel.class);
        smodel.getAllSorted().observe(getActivity(), new Observer<List<SymptomsRecord>>() {
            @Override
            public void onChanged(List<SymptomsRecord> symptomRecords) {
                //something in db has changed, update
                symptomDbChanged = true;
                changedRecords = symptomRecords;
                Constants.symptomRecords = symptomRecords;
                Log.e("symptom", "symptomtracker - symptom list changed");
                if (Constants.CurrentFragment.toString().toLowerCase().contains("health")) {
                    markDays();
                    Log.e("symptom", "symptomtracker - symptom list changing");
                    updateFeaturedDate(cal.getSelectedDate(), getContext(), getActivity());
                    symptomDbChanged = false;
                }
            }
        });

        return view;
    }

    public void initCal() {
        cal = view.findViewById(R.id.calendarView);
        /////////////////////////////////////////////////////////////////
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        int days = prefs.getInt(getContext().getString(R.string.infection_window_in_days_pkeys), Constants.DefaultInfectionWindowInDays);
        calendar.add(Calendar.DATE, -days);
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
                Log.e("symptom","on date selected "+date.toString());
                Constants.symptomTrackerMonthCalendar.set(date.getYear(),date.getMonth()-1,date.getDay());
                updateFeaturedDate(date, getContext(), getActivity());
            }
        });
    }

    public void markDays() {
        Log.e("symptoms","update days");
        markedDays.clear();
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");

        for (SymptomsRecord record : Constants.symptomRecords) {
            Date ts = new Date(record.getTs());
            markedDays.add(CalendarDay.from(Integer.parseInt(year.format(ts)),
                    Integer.parseInt(month.format(ts)),
                    Integer.parseInt(day.format(ts))));
        }
        Log.e("mark","marked days "+markedDays.size());
        cal.addDecorators(new EventDecorator(getContext().getColor(R.color.purpleDark),
                markedDays));
    }

    private class EventDecorator implements DayViewDecorator {

        private final int color;
        private final HashSet<CalendarDay> dates;

        public EventDecorator(int color, Collection<CalendarDay> dates) {
            this.color = color;
            this.dates = new HashSet<>(dates);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return dates.contains(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new DotSpan(5, color));
        }
    }

    public static void updateFeaturedDate(CalendarDay calDay, Context cxt, Activity av) {
        cal.setSelectedDate(calDay);
        cal.setCurrentDate(calDay);
        Log.e("symptom","update featured date");
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        try {
            String dateStr = calDay.getYear() + "/" + calDay.getMonth() + "/" + calDay.getDay();
            Date dd = format.parse(dateStr);
            Log.e("symptom","updating "+dateStr);
            SymptomUtils.updateTodaysLogs(view, changedRecords, cxt,av, dd, "tracker");
            symptomHistoryAdapter.setRecords(changedRecords, view);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("health","symptom tracker fragment onresume");

        if (Constants.menu != null && Constants.menu.findItem(R.id.mybutton) != null) {
            Constants.menu.findItem(R.id.mybutton).setVisible(true);
        }
        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
//        Constants.CurrentFragment = this;
        if (symptomDbChanged) {
            Log.e("symptoms","db changed ");
            updateFeaturedDate(cal.getSelectedDate(), getContext(), getActivity());
            symptomDbChanged = false;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Constants.SymptomTrackerFragment = this;
        Constants.HealthFragmentState = this;
    }
}
