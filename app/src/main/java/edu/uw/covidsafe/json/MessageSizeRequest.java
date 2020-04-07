package edu.uw.covidsafe.json;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.comms.NetworkConstant;

public class MessageSizeRequest {

    Region region;
    long last_query_time;

    public static MessageSizeRequest parse(JSONObject obj) throws JSONException {
        MessageSizeRequest messageSizeRequest = new MessageSizeRequest();
        if (obj.has("region")) {
            messageSizeRequest.region = Region.parse(obj.getJSONObject("region"));
        }
        if (obj.has("last_query_time")) {
            messageSizeRequest.last_query_time = obj.getLong("last_query_time");
        }
        return messageSizeRequest;
    }

    public static JSONObject toJson(double lat, double longi, int precision, long ts) throws JSONException {
        JSONObject messageSizeRequest = new JSONObject();
        messageSizeRequest.put("region", Region.toJson(lat, longi, precision));
        messageSizeRequest.put("last_query_time", ts);
        return messageSizeRequest;
    }

    public static String toHttpString(double lat, double longi, int precision, long ts) {
        return NetworkConstant.BASE_URL+"Messages/"+String.format("Size?lat=%f&lon=%f&precision=%d&lastTimestamp=%d",lat,longi,precision,ts);
    }
}
