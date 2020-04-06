package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.comms.NetworkConstant;

public class MessageListRequest {

    Region region;
    long last_query_time;

    public static MessageListRequest parse(JSONObject obj) throws JSONException {
        MessageListRequest messageListRequest = new MessageListRequest();
        if (obj.has("region")) {
            messageListRequest.region = Region.parse(obj.getJSONObject("region"));
        }
        if (obj.has("last_query_time")) {
            messageListRequest.last_query_time = obj.getLong("last_query_time");
        }
        return messageListRequest;
    }

    public static JSONObject toJson(double lat, double longi, int precision, long lastQueryTime) throws JSONException {
        JSONObject messageListRequest = new JSONObject();
        messageListRequest.put("region", Region.toJson(lat, longi, precision));
        messageListRequest.put("last_query_time", lastQueryTime);
        return messageListRequest;
    }

    public static String toHttpString(double lat, double longi, int precision, long lastQueryTime) {
        return NetworkConstant.BASE_URL+"Messages/"+String.format("List?lat=%f&lon=%f&precision=%d&lastTimestamp=%d",lat,longi,precision,lastQueryTime);
    }
}
