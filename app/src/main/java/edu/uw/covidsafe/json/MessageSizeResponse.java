package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class MessageSizeResponse {

    public int size_of_query_response;

    public static MessageSizeResponse parse(JsonObject obj) {
        MessageSizeResponse response = new MessageSizeResponse();
        if (obj.has("size_of_query_response")) {
            response.size_of_query_response = obj.get("size_of_query_response").getAsInt();
        }
        return response;
    }
}
