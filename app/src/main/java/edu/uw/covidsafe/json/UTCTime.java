package edu.uw.covidsafe.json;

import android.util.Log;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

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

    public static UTCTime parse(JSONObject obj) throws JSONException {
        UTCTime time = new UTCTime();
        if (obj.has("year")) {
            obj.getInt("year");
        }
        if (obj.has("month")) {
            obj.getInt("month");
        }
        if (obj.has("day")) {
            obj.getInt("day");
        }
        if (obj.has("hour")) {
            obj.getInt("hour");
        }
        if (obj.has("minute")) {
            obj.getInt("minute");
        }
        if (obj.has("second")) {
            obj.getInt("second");
        }
        if (obj.has("millisecond")) {
            obj.getInt("millisecond");
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

    public JSONObject toJson() throws JSONException {
        JSONObject utcTime = new JSONObject();
        utcTime.put("day",day);
        utcTime.put("month",month);
        utcTime.put("year",year);
        utcTime.put("hour",hour);
        utcTime.put("minute",minute);
        utcTime.put("second",second);
        utcTime.put("millisecond",millisecond);
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
