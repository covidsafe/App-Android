package edu.uw.covidsafe.json;

import org.json.JSONException;
import org.json.JSONObject;

public class Region {
    double latitudePrefix;
    double longitudePrefix;
    int precision;

    public static Region parse(JSONObject obj) throws JSONException {
        Region region = new Region();
        if (obj.has("latitudePrefix")) {
            region.latitudePrefix = obj.getDouble("latitudePrefix");
        }
        if (obj.has("longitudePrefix")) {
            region.longitudePrefix = obj.getDouble("longitudePrefix");
        }
        if (obj.has("precision")) {
            region.precision = obj.getInt("precision");
        }
        return region;
    }

    public static JSONObject toJson(double lat, double longi, double precision) throws JSONException {
        JSONObject region = new JSONObject();
        region.put("latitudePrefix", lat);
        region.put("longitudePrefix", longi);
        region.put("precision", precision);
        return region;
    }
}
