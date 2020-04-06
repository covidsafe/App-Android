package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Area {
    public Location location;
    public float radius_meters;
    public long begin_time;
    public long end_time;

    public static Area parse(JSONObject obj) throws JSONException {
        Area area = new Area();
        if (obj.has("location")) {
            area.location = Location.parse(obj.getJSONObject("location"));
        }
        if (obj.has("radius_meters")) {
            area.radius_meters = (float)obj.getDouble("radius_meters");
        }
        if (obj.has("begin_time")) {
            area.begin_time = obj.getLong("begin_time");
        }
        if (obj.has("end_time")) {
            area.end_time = obj.getLong("end_time");
        }
        return area;
    }
}
