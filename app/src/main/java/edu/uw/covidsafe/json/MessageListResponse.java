package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageListResponse {
    public MessageInfo[] messageInfo;
    public long query_time = 2;

    public static MessageListResponse parse(JSONObject obj) throws JSONException {
        MessageListResponse messageListResponse = new MessageListResponse();
        if (obj.has("messageInfo")) {
            JSONArray arr = obj.getJSONArray("messageInfo");
            messageListResponse.messageInfo = new MessageInfo[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                messageListResponse.messageInfo[i] = MessageInfo.parse(arr.getJSONObject(i));
            }
        }
        if (obj.has("query_time")) {
            messageListResponse.query_time = obj.getLong("query_time");
        }
        return messageListResponse;
    }
}
