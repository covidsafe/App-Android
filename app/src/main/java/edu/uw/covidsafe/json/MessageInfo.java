package edu.uw.covidsafe.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class MessageInfo {
    public String message_id;
    public UTCTime message_timestamp;

    public MessageInfo(String message_id, JsonObject time) {
        this.message_id = message_id;
        this.message_timestamp = UTCTime.parse(time);
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        if (obj.has("message_id")) {
            obj.addProperty("message_id", message_id);
        }
        if (obj.has("message_timestamp")) {
            obj.add("message_timestamp", message_timestamp.toJson());
        }
        return obj;
    }

    public static MessageInfo parse(JsonObject obj) {
        MessageInfo messageInfo = new MessageInfo(obj.get("message_id").getAsString(),
                obj.get("message_id").getAsJsonObject());
        return messageInfo;
    }
}
