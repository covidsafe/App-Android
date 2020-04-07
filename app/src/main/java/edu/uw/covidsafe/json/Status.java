package edu.uw.covidsafe.json;

import org.json.JSONException;
import org.json.JSONObject;

public class Status {
    int status_code;
    String status_message;

    public static Status parse(JSONObject obj) throws JSONException {
        Status status = new Status();
        if (obj.has("status_code")) {
            status.status_code = obj.getInt("status_code");
        }
        if (obj.has("status_message")) {
            status.status_message = obj.getString("status_message");
        }
        return status;
    }
}
