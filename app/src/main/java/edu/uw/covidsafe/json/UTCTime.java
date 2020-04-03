package edu.uw.covidsafe.json;

import android.util.Log;

import com.google.gson.JsonObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class UTCTime {
    // json properties
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second;
    public int millisecond;

    public static UTCTime parse(JsonObject obj) {
        UTCTime time = new UTCTime();
        if (obj.has("year")) {
            obj.get("year").getAsInt();
        }
        if (obj.has("month")) {
            obj.get("month").getAsInt();
        }
        if (obj.has("day")) {
            obj.get("day").getAsInt();
        }
        if (obj.has("hour")) {
            obj.get("hour").getAsInt();
        }
        if (obj.has("minute")) {
            obj.get("minute").getAsInt();
        }
        if (obj.has("second")) {
            obj.get("second").getAsInt();
        }
        if (obj.has("millisecond")) {
            obj.get("millisecond").getAsInt();
        }
        return time;
    }

    public long toLong() {
        try {
            String tt = day + "," + month + "," + year + "," + hour + "," + minute + "," + second + "," + millisecond;
            DateFormat format = new SimpleDateFormat("dd,MM,yyyy,HH,mm,ss,SSS");
            format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            Date dd = format.parse(tt);
            return dd.getTime();
        }
        catch(Exception e) {
            Log.e("err",e.getMessage());
        }
        return 0;
    }

    public JsonObject toJson() {
        JsonObject utcTime = new JsonObject();
        utcTime.addProperty("day",day);
        utcTime.addProperty("month",month);
        utcTime.addProperty("year",year);
        utcTime.addProperty("hour",hour);
        utcTime.addProperty("minute",minute);
        utcTime.addProperty("second",second);
        utcTime.addProperty("millisecond",millisecond);
        return utcTime;
    }

    public static JsonObject toJson(long ts) {
        Date dd = new Date(ts);
        DateFormat format = new SimpleDateFormat("dd,MM,yyyy,HH,mm,ss,SSS");
        format.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));

        String ss = format.format(dd);
        String[] elts = ss.split(",");

        JsonObject utcTime = new JsonObject();
        utcTime.addProperty("day",elts[0]);
        utcTime.addProperty("month",elts[1]);
        utcTime.addProperty("year",elts[2]);
        utcTime.addProperty("hour",elts[3]);
        utcTime.addProperty("minute",elts[4]);
        utcTime.addProperty("second",elts[5]);
        utcTime.addProperty("millisecond",elts[6]);

        return utcTime;
    }
}
