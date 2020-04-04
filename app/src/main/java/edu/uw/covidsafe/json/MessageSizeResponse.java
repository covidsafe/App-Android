package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageSizeResponse {

    public int size_of_query_response;

    public static MessageSizeResponse parse(JSONObject obj) throws JSONException {
        MessageSizeResponse response = new MessageSizeResponse();
        if (obj.has("size_of_query_response")) {
            response.size_of_query_response = obj.getInt("size_of_query_response");
        }
        return response;
    }
}
