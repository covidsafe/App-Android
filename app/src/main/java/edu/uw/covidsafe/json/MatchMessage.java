package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MatchMessage {
    public String boolExpression;
    public AreaMatch[] areaMatches;
    public BlueToothSeed[] bluetoothSeeds;
    public String bluetooth_match_message;

    public JSONObject toJSON() throws JSONException {
        JSONObject matchMessage = new JSONObject();

        if (bluetoothSeeds != null) {
            JSONArray arr = new JSONArray();
            for (BlueToothSeed bseed : bluetoothSeeds) {
                arr.put(bseed.toJson());
            }
            matchMessage.put("bluetoothSeeds", arr);
        }

        if (areaMatches != null) {
            JSONArray arr2 = new JSONArray();
            for (AreaMatch amatch : areaMatches) {
                arr2.put(amatch.toJson());
            }
            matchMessage.put("areaMatches", arr2);
        }
        if (bluetooth_match_message != null) {
            matchMessage.put("bluetooth_match_message", bluetooth_match_message);
        }

        return matchMessage;
    }

    public static MatchMessage parse(JSONObject obj) throws JSONException {
        MatchMessage matchMessage = new MatchMessage();
        if (obj.has("boolExpression")) {
            matchMessage.boolExpression = obj.getString("boolExpression");
        }
        if (obj.has("areaMatches")) {
            JSONArray arr = obj.getJSONArray("areaMatches");
            matchMessage.areaMatches = new AreaMatch[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                matchMessage.areaMatches[i] = AreaMatch.parse(arr.getJSONObject(i));
            }
        }
        if (obj.has("bluetoothSeeds")) {
            JSONArray arr = obj.getJSONArray("bluetoothSeeds");
            matchMessage.bluetoothSeeds = new BlueToothSeed[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                matchMessage.bluetoothSeeds[i] = BlueToothSeed.parse(arr.getJSONObject(i));
            }
        }
        if (obj.has("bluetooth_match_message")) {
            matchMessage.bluetooth_match_message = obj.getString("bluetooth_match_message");
        }
        return matchMessage;
    }
}
