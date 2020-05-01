package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchMessageResponse {
    public MatchMessage[] match_messages;

    public static MatchMessageResponse parse(JSONObject obj) throws JSONException {
        MatchMessageResponse matchMessageResponse = new MatchMessageResponse();
        if (obj.has("match_messages")) {
            JSONArray arr = obj.getJSONArray("match_messages");
            matchMessageResponse.match_messages = new MatchMessage[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                matchMessageResponse.match_messages[i] = MatchMessage.parse(arr.getJSONObject(i));
            }
        }
        return matchMessageResponse;
    }
}
