package edu.uw.covidsafe.json;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {
    public double latitude;
    public double longitude;

    public static Location parse(JSONObject obj) throws JSONException {
        Location loc = new Location();
        if (obj.has("latitude")) {
            loc.latitude = obj.getDouble("latitude");
        }
        if (obj.has("longitude")) {
            loc.longitude = obj.getDouble("longitude");
        }
        return loc;
    }
}
