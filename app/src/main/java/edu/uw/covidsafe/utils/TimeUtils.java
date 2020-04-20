package edu.uw.covidsafe.utils;

import android.util.Log;

import com.instacart.library.truetime.TrueTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TimeUtils {

    public static long getNDaysBack(int N) {
        Date dd = new Date(TimeUtils.getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dd);
        calendar.add(Calendar.DATE, N);
        return calendar.getTime().getTime();
    }

    public static long getTime() {
//        Log.e("truetime","get time "+TrueTime.isInitialized());
        if (!TrueTime.isInitialized()) {
            return System.currentTimeMillis();
        }
        else {
            return TrueTime.now().getTime();
        }
    }

    // returns the prior synchronized interval time
    // e.g. 2:05 => 2:00
    // used for regenerating initial seed upon submission
    // also used for generating seeds to an exact timestamp
    public static long getPreviousGenerationTimestamp(long ts) {
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");

        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");

        Date dd = new Date(ts);
        int year = Integer.parseInt(yearFormat.format(dd));
        int month = Integer.parseInt(monthFormat.format(dd));
        int day = Integer.parseInt(dayFormat.format(dd));

        int hour = Integer.parseInt(hourFormat.format(dd));
        int minute = Integer.parseInt(minuteFormat.format(dd));

        List<Integer> intervals = new LinkedList<>();
        for (int i = 0; i < 60/Constants.UUIDGenerationIntervalInMinutes; i++) {
            intervals.add(i*Constants.UUIDGenerationIntervalInMinutes);
        }

        int breakpoint = 0;
        for (int i = 0; i < intervals.size(); i++) {
            if (minute >= intervals.get(i)) {
                breakpoint = intervals.get(i);
            }
        }
        String targetDate = year+"/"+month+"/"+day+" "+hour+":"+breakpoint;

        SimpleDateFormat fullDate = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        try {
            return fullDate.parse(targetDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // this calculates the delay till the nearest 15 minute interval when we should randomize the ids
    // e.g. 10:05 => 10:15
    // and calculates how long till then (e.g. 10 minutes)
    public static int getDelayTilllUUIDBroadcastInSeconds(long ts) {
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

        Date dd = new Date(ts);

        int minute = Integer.parseInt(minuteFormat.format(dd));
        int second = Integer.parseInt(secondFormat.format(dd));

        List<Integer> intervals = new LinkedList<>();
        for (int i = 0; i < 60/Constants.UUIDGenerationIntervalInMinutes; i++) {
            intervals.add(i*Constants.UUIDGenerationIntervalInMinutes);
        }

        int delay = 0;
        if (second == 0 && intervals.contains(minute)) {
            delay = 0;
        }
        else {
            int closestIntervalIndex = 0;
            if (minute < intervals.get(intervals.size()-1)) {
                for (int i = 0; i < intervals.size(); i++) {
                    if (minute >= intervals.get(i)) {
                        closestIntervalIndex = i+1;
                    }
                }
            }

            int targetMinute = intervals.get(closestIntervalIndex);

            int secondDelay = 60-second;
            int minuteDelay = targetMinute-minute-1;
            if (targetMinute == 0) {
                secondDelay = 60-second;
                minuteDelay = 60-minute-1;
            }
            delay = minuteDelay*60+secondDelay;
        }
        return delay;
    }
}
