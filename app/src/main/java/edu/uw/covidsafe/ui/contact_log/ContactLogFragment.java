package edu.uw.covidsafe.ui.contact_log;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import edu.uw.covidsafe.gps.GpsDbModel;
import edu.uw.covidsafe.gps.GpsHistoryRecyclerViewAdapter;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class ContactLogFragment extends Fragment {

    static View view;
    boolean gpsDbChanged = false;
    static List<GpsRecord> changedRecords;
    static GpsHistoryRecyclerViewAdapter gpsHistoryAdapter;
    static MaterialCalendarView cal;

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
        Constants.menu.findItem(R.id.mybutton).setVisible(true);

        ((MainActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getActivity().getResources().getColor(R.color.white)));
        ((MainActivity) getActivity()).getSupportActionBar().setShowHideAnimationEnabled(false);
        ((MainActivity) getActivity()).getSupportActionBar().show();
        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        String header_str = getActivity().getString(R.string.contact_log_header_text);
        if (Constants.PUBLIC_DEMO) {
            header_str = getActivity().getString(R.string.contact_log_header_text_demo);
        }
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(header_str));

        RecyclerView rview = view.findViewById(R.id.recyclerViewGpsHistory);
        gpsHistoryAdapter = new GpsHistoryRecyclerViewAdapter(getActivity(),getActivity(), view);
        rview.setAdapter(gpsHistoryAdapter);
        rview.setLayoutManager(new LinearLayoutManager(getActivity()));
        initCal();

        GpsDbModel smodel = ViewModelProviders.of(getActivity()).get(GpsDbModel.class);
        smodel.getAllSorted().observe(getActivity(), new Observer<List<GpsRecord>>() {
            @Override
            public void onChanged(List<GpsRecord> gpsRecords) {
                //something in db has changed, update
                gpsDbChanged = true;
                changedRecords = gpsRecords;
                Log.e("contact","db on changed "+(changedRecords.size()));
                if (Constants.CurrentFragment.toString().toLowerCase().contains("contact")) {
                    Log.e("contact","db on changing");
                    updateLocationView(cal.getSelectedDate(), getContext());
                    gpsDbChanged = false;
                }
            }
        });

        return view;
    }

    public void initCal() {
        cal = view.findViewById(R.id.calendarView);
        /////////////////////////////////////////////////////////////////
        SharedPreferences prefs = getContext().getSharedPreferences(Constants.SHARED_PREFENCE_NAME, Context.MODE_PRIVATE);
        int days = prefs.getInt(getContext().getString(R.string.infection_window_in_days_pkeys), Constants.DefaultInfectionWindowInDays);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
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
                Log.e("contact","on date selected "+date.toString());
                Log.e("contact","on date selected "+date.getYear()+","+date.getMonth()+","+date.getDay());
                Constants.contactLogMonthCalendar.set(date.getYear(),date.getMonth()-1,date.getDay());
                updateLocationView(date, getContext());
            }
        });
    }

    public static void updateLocationView(CalendarDay dd, Context cxt) {
        cal.setCurrentDate(dd);
        cal.setSelectedDate(dd);

        markDays(cxt);
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
            SimpleDateFormat month = new SimpleDateFormat("MM");
            SimpleDateFormat day = new SimpleDateFormat("dd");

            Date date = format.parse(dd.getYear() + "/" + dd.getMonth() + "/" + dd.getDay());

            SimpleDateFormat format2 = new SimpleDateFormat("EEEE, MMMM dd");
            TextView dayTitle = (TextView)view.findViewById(R.id.dayTitle);
            dayTitle.setText(format2.format(date));

            List<GpsRecord> filtRecords = new LinkedList<GpsRecord>();
            for (GpsRecord record : changedRecords) {
                boolean dayMatch = Integer.parseInt(day.format(record.getTs())) == dd.getDay();
                boolean monthMatch = Integer.parseInt(month.format(record.getTs())) == dd.getMonth();
                if (dayMatch&&monthMatch) {
                    filtRecords.add(record);
                }
                else if (filtRecords.size() > 0) {
                    break;
                }
            }
            gpsHistoryAdapter.setRecords(filtRecords, cxt);
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    public static void markDays(Context cxt) {
        Log.e("symptoms","update days");
        List<CalendarDay> markedDays = new LinkedList<>();
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");

        for (GpsRecord record : changedRecords) {
            Date ts = new Date(record.getTs());
            markedDays.add(CalendarDay.from(Integer.parseInt(year.format(ts)),
                    Integer.parseInt(month.format(ts)),
                    Integer.parseInt(day.format(ts))));
        }

        if (cxt != null) {
            cal.addDecorators(new ContactLogFragment.EventDecorator(cxt.getColor(R.color.purpleDark),
                    markedDays));
        }
    }

    private static class EventDecorator implements DayViewDecorator {

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

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.ContactLogFragment = this;

        if (gpsDbChanged) {
            Log.e("contact","db changed ");
            updateLocationView(cal.getSelectedDate(), getContext());
            gpsDbChanged = false;
        }
    }
}
