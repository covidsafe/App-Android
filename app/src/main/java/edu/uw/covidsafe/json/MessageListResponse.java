package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MessageListResponse {
    public MessageInfo[] messageInfos;

    public static MessageListResponse parse(JsonObject obj) {
        MessageListResponse response = new MessageListResponse();

        if (obj.has("message_info")) {
            JsonArray arr = obj.getAsJsonArray("message_info");
            response.messageInfos = new MessageInfo[arr.size()];

            for (int i = 0; i < arr.size(); i++) {
                response.messageInfos[i] = MessageInfo.parse(arr.get(i).getAsJsonObject());
            }
        }
        return response;
    }
}
