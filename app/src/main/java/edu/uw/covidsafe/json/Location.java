package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class Location {
    public double lattitude;
    public double longitude;

    public static Location parse(JSONObject obj) throws JSONException {
        Location loc = new Location();
        if (obj.has("lattitude")) {
            loc.lattitude = obj.getDouble("lattitude");
        }
        if (obj.has("longitude")) {
            loc.longitude = obj.getDouble("longitude");
        }
        return loc;
    }
}
