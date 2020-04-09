package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.comms.NetworkConstant;

public class AreaMatch {

    public Area[] areas;
    public String userMessage;

    public JSONObject toJson() throws JSONException {
        JSONObject area_match = new JSONObject();
        JSONArray arr = new JSONArray();
        for (Area area : areas) {
            arr.put(area.toJson());
        }
        area_match.put("areas", arr);
        area_match.put("userMessage", userMessage);
        return area_match;
    }

    public static AreaMatch parse(JSONObject obj) throws JSONException {
        AreaMatch areaMatch = new AreaMatch();
        if (obj.has("areas")) {
            JSONArray array = obj.getJSONArray("areas");
            areaMatch.areas = new Area[array.length()];
            for (int i = 0; i < array.length(); i++) {
                areaMatch.areas[i] = Area.parse(array.getJSONObject(i));
            }
        }
        if (obj.has("userMessage")) {
            areaMatch.userMessage = obj.getString("userMessage");
        }
        return areaMatch;
    }

    public static String toHttpString() {
        return NetworkConstant.BASE_URL+"Messages/AreaReport";
    }
}
