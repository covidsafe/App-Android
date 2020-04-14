package edu.uw.covidsafe.utils;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class TimeUtilsTest {

    @Test
    public void getDelayTilllUUIDBroadcastInSeconds() throws ParseException {
//        testGetDelayTilllUUIDBroadcastInSeconds(System.currentTimeMillis());
//        SimpleDateFormat fullDate = new SimpleDateFormat("yyyy/MM/dd hh:mm.ss");
//        String ss = "2020/01/01 12:01.1";
//        getPreviousGenerationTimestamp(fullDate.parse(ss).getTime());
        System.out.println(Math.ceil(23/15.0));
        System.out.println(Math.ceil(8/15.0));
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
        System.out.println(targetDate);
        SimpleDateFormat fullDate = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        try {
            return fullDate.parse(targetDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void testGetDelayTilllUUIDBroadcastInSeconds(long ts) {
        SimpleDateFormat hourFormat = new SimpleDateFormat("hh");
        SimpleDateFormat minuteFormat = new SimpleDateFormat("mm");
        SimpleDateFormat secondFormat = new SimpleDateFormat("ss");

        Date dd = new Date(ts);

        int hour = Integer.parseInt(hourFormat.format(dd));
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
            int targetHour = hour;

            if (targetMinute == 0) {
                targetHour = (targetHour+1)%12;
            }

            int secondDelay = 60-second;
            int minuteDelay = targetMinute-minute-1;
            if (targetMinute == 0) {
                secondDelay = 60-second;
                minuteDelay = 60-minute-1;
            }
            delay = minuteDelay*60+secondDelay;
            System.out.println(hour+":"+minute+"."+second);
            System.out.println(targetHour+":"+targetMinute+".0");
            System.out.println(minuteDelay+","+secondDelay);
        }
    }
}
