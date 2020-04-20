package edu.uw.covidsafe.ui.contact_log;

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
import edu.uw.covidsafe.gps.GpsDbRecordRepository;
import edu.uw.covidsafe.gps.GpsHistoryRecyclerViewAdapter;
import edu.uw.covidsafe.gps.GpsRecord;
import edu.uw.covidsafe.symptoms.SymptomDbModel;
import edu.uw.covidsafe.symptoms.SymptomHistoryRecyclerViewAdapter;
import edu.uw.covidsafe.symptoms.SymptomTrackerFragment;
import edu.uw.covidsafe.symptoms.SymptomsRecord;
import edu.uw.covidsafe.ui.MainActivity;
import edu.uw.covidsafe.utils.Constants;

public class ContactLogFragment extends Fragment {

    View view;
    boolean gpsDbChanged = false;
    List<GpsRecord> changedRecords;
    GpsHistoryRecyclerViewAdapter gpsHistoryAdapter;
    MaterialCalendarView cal;

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
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(Html.fromHtml(getActivity().getString(R.string.contact_log_header_text)));

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
                markDays();
                Log.e("contact","db on changed "+(changedRecords.size()));
                if (Constants.CurrentFragment.toString().toLowerCase().contains("contact")) {
                    Log.e("contact","db on changing");
                    updateLocationView(cal.getSelectedDate());
                    gpsDbChanged = false;
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
                Log.e("symptom","on date selected "+date.toString());
                updateLocationView(date);
            }
        });
    }

    public void updateLocationView(CalendarDay dd) {
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
            gpsHistoryAdapter.setRecords(filtRecords, getContext());
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
    }

    public void markDays() {
        Log.e("symptoms","update days");
        List<CalendarDay> markedDays = new LinkedList<>();
        SimpleDateFormat year = new SimpleDateFormat("yyyy");
        SimpleDateFormat month = new SimpleDateFormat("MM");
        SimpleDateFormat day = new SimpleDateFormat("dd");

        for (SymptomsRecord record : Constants.symptomRecords) {
            Date ts = new Date(record.getTs());
            markedDays.add(CalendarDay.from(Integer.parseInt(year.format(ts)),
                    Integer.parseInt(month.format(ts)),
                    Integer.parseInt(day.format(ts))));
        }

        cal.addDecorators(new ContactLogFragment.EventDecorator(getContext().getColor(R.color.purpleDark),
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

    @Override
    public void onResume() {
        super.onResume();
        Constants.CurrentFragment = this;
        Constants.ContactLogFragment = this;

        if (gpsDbChanged) {
            Log.e("contact","db changed ");
            updateLocationView(cal.getSelectedDate());
            gpsDbChanged = false;
        }
    }
}
