package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class Area {
    public Location location;
    public float radius_meters;
    public UTCTime begin_time;
    public UTCTime end_time;

    public static Area parse(JsonObject obj) {
        Area area = new Area();
        if (obj.has("location")) {
            area.location = Location.parse(obj.get("location").getAsJsonObject());
        }
        if (obj.has("radius_meters")) {
            area.radius_meters = obj.get("radius_meters").getAsFloat();
        }
        if (obj.has("begin_time")) {
            area.begin_time = UTCTime.parse(obj.get("begin_time").getAsJsonObject());
        }
        if (obj.has("end_time")) {
            area.end_time = UTCTime.parse(obj.get("end_time").getAsJsonObject());
        }
        return area;
    }
}
