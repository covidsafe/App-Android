package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class MessageListRequest {
    public static JsonObject toJson(double lat, double longi, int precision, long lastQueryTime) {
        JsonObject messageListRequest = new JsonObject();
        messageListRequest.add("region", Region.toJson(lat, longi, precision));
        messageListRequest.addProperty("last_query_time", lastQueryTime);
        return messageListRequest;
    }
}
