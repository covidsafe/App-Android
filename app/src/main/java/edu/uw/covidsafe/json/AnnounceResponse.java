package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class AnnounceResponse {
    public static AnnounceResponse parse(JsonObject obj) {
        return new AnnounceResponse();
    }
}
