package edu.uw.covidsafe.json;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.ui.onboarding.PagerFragment;

public class Location {
    public double latitude;
    public double longitude;

    public Location() {

    }

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject area_match = new JSONObject();
        area_match.put("latitude", latitude);
        area_match.put("longitude",longitude);
        return area_match;
    }

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
