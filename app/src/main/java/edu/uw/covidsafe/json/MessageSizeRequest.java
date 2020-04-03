package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class MessageSizeRequest {
    public static JsonObject toJson(double lat, double longi, int precision, long ts) {
        JsonObject messageSizeRequest = new JsonObject();
        messageSizeRequest.add("region", Region.toJson(lat, longi, precision));
        messageSizeRequest.add("last_query_time", UTCTime.toJson(ts));
        return messageSizeRequest;
    }
}
