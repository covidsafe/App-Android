package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MessageRequest {
    public static JsonObject toJson(MessageInfo[] messageInfos) {
        JsonObject obj = new JsonObject();
        JsonArray array = new JsonArray();
        for (MessageInfo messageInfo : messageInfos) {
            array.add(messageInfo.toJson());
        }
        obj.add("requested_queries", array);
        return obj;
    }
}
