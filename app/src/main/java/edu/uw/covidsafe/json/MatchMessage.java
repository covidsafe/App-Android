package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchMessage {
    public String bool_expression;
    public AreaMatch[] area_matches;
    public BluetoothMatch[] bluetooth_matches;

    public static MatchMessage parse(JSONObject obj) throws JSONException {
        MatchMessage matchMessage = new MatchMessage();
        if (obj.has("bool_expression")) {
            matchMessage.bool_expression = obj.getString("bool_expression");
        }
        if (obj.has("area_matches")) {
            JSONArray arr = obj.getJSONArray("area_match");
            matchMessage.area_matches = new AreaMatch[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                matchMessage.area_matches[i] = AreaMatch.parse(arr.getJSONObject(i));
            }
        }
        if (obj.has("bluetooth_matches")) {
            JSONArray arr = obj.getJSONArray("bluetooth_matches");
            matchMessage.bluetooth_matches = new BluetoothMatch[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                matchMessage.bluetooth_matches[i] = BluetoothMatch.parse(arr.getJSONObject(i));
            }
        }
        return matchMessage;
    }

    public static JSONObject toJson(String[] seed, long[] ts) throws JSONException {
        JSONObject matchMessage = new JSONObject();
        matchMessage.put("match_protocol_version", 1);

        JSONArray arr = new JSONArray();
        for (int i = 0; i < seed.length; i++) {
            arr.put(BlueToothSeed.toJson(seed[i], ts[i]));
        }

        matchMessage.put("bluetooth_match", arr);
        return matchMessage;
    }
}
