package edu.uw.covidsafe.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageInfo {
    public String message_id;
    public UTCTime message_timestamp;

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        if (obj.has("message_id")) {
            obj.put("message_id", message_id);
        }
        if (obj.has("message_timestamp")) {
            obj.put("message_timestamp", message_timestamp.toJson());
        }
        return obj;
    }

    public static MessageInfo parse(JSONObject obj) throws JSONException {
        MessageInfo messageInfo = new MessageInfo();
        if (obj.has("message_id")) {
            messageInfo.message_id = obj.getString("message_id");
        }
        if (obj.has("message_timestamp")) {
            messageInfo.message_timestamp = UTCTime.parse(obj.getJSONObject("message_timestamp"));
        }
        return messageInfo;
    }
}
