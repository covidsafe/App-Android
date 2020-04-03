package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class AnnounceRequest {
    MatchMessage matchMessage;
    Region region;

    public static JsonObject toJson(String[] seed, long[] ts,
                                    double lat, double longi,
                                    int precision) {
        JsonObject announceRequest = new JsonObject();
        JsonObject matchMessage = MatchMessage.toJson(seed, ts);
        JsonObject region = Region.toJson(lat, longi, precision);
        announceRequest.add("match_message", matchMessage);
        announceRequest.add("region", region);
        return announceRequest;
    }
}
