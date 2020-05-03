package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Area {
    public Location location;
    public float radiusMeters;
    public long beginTime;
    public long endTime;

    public Area() {

    }

    public Area(double lat, double longi, float radiusMeters, long btime, long etime) {
        this.location = new Location(lat, longi);
        this.radiusMeters = radiusMeters;
        this.beginTime = btime;
        this.endTime = etime;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject area_match = new JSONObject();
        area_match.put("location", location.toJson());
        area_match.put("radiusMeters",radiusMeters);
        area_match.put("beginTime",beginTime);
        area_match.put("endTime",endTime);
        return area_match;
    }

    public static Area parse(JSONObject obj) throws JSONException {
        Area area = new Area();
        if (obj.has("location")) {
            area.location = Location.parse(obj.getJSONObject("location"));
        }
        if (obj.has("radiusMeters")) {
            area.radiusMeters = (float)obj.getDouble("radiusMeters");
        }
        if (obj.has("beginTime")) {
            area.beginTime = obj.getLong("beginTime");
        }
        if (obj.has("endTime")) {
            area.endTime = obj.getLong("endTime");
        }
        return area;
    }
}
