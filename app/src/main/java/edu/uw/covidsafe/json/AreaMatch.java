package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AreaMatch {

    public Area[] areas;
    public String user_message;

    public static AreaMatch parse(JSONObject obj) throws JSONException {
        AreaMatch areaMatch = new AreaMatch();
        if (obj.has("areas")) {
            JSONArray array = obj.getJSONArray("areas");
            areaMatch.areas = new Area[array.length()];
            for (int i = 0; i < array.length(); i++) {
                areaMatch.areas[i] = Area.parse(array.getJSONObject(i));
            }
        }
        if (obj.has("user_message")) {
            areaMatch.user_message = obj.getString("user_message");
        }
        return areaMatch;
    }
}
