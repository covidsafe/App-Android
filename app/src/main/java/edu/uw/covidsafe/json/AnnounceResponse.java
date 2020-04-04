package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class AnnounceResponse {
    Status status;
    public static AnnounceResponse parse(JSONObject obj) throws JSONException {
        AnnounceResponse resp = new AnnounceResponse();
        if (obj.has("status")) {
            resp.status = Status.parse(obj.getJSONObject("status"));
        }
        return resp;
    }
}
