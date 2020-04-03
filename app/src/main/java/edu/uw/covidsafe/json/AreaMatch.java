package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class AreaMatch {

    public Area[] areas;
    public float proximity_radius_meters;
    public int duration_tolerance_secs;

    public static AreaMatch parse(JsonObject obj) {
        AreaMatch areaMatch = new AreaMatch();
        if (obj.has("proximity_radius_meters")) {
            areaMatch.proximity_radius_meters = obj.get("proximity_radius_meters").getAsFloat();
        }
        if (obj.has("duration_tolerance_secs")) {
            areaMatch.duration_tolerance_secs = obj.get("duration_tolerance_secs").getAsInt();
        }
        if (obj.has("areas")) {
            JsonArray array = obj.get("areas").getAsJsonArray();
            for (int i = 0; i < array.size(); i++) {
                areaMatch.areas[i] = Area.parse(array.get(i).getAsJsonObject());
            }
        }
        return areaMatch;
    }
}
