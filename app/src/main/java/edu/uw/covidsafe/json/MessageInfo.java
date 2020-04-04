package edu.uw.covidsafe.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageInfo {
    public String MessageId;
    public UTCTime MessageTimestamp;

    public JSONObject toJson() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("MessageId", MessageId);
        obj.put("MessageTimestamp", MessageTimestamp.toJson());
        return obj;
    }

    public static MessageInfo parse(JSONObject obj) throws JSONException {
        MessageInfo messageInfo = new MessageInfo();
        if (obj.has("messageId")) {
            messageInfo.MessageId = obj.getString("messageId");
        }
        if (obj.has("messageTimestamp")) {
            messageInfo.MessageTimestamp = UTCTime.parse(obj.getJSONObject("messageTimestamp"));
        }
        return messageInfo;
    }
}
