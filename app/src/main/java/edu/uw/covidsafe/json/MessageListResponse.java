package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageListResponse {
    public MessageInfo[] message_info;

    public static MessageListResponse parse(JSONObject obj) throws JSONException {
        MessageListResponse messageListResponse = new MessageListResponse();
        if (obj.has("message_info")) {
            JSONArray arr = obj.getJSONArray("message_info");
            messageListResponse.message_info = new MessageInfo[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                messageListResponse.message_info[i] = MessageInfo.parse(arr.getJSONObject(i));
            }
        }
        return messageListResponse;
    }
}
