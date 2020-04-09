package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.comms.NetworkConstant;

public class MatchMessage {
    public String boolExpression;
    public AreaMatch[] areaMatches;
    public BluetoothMatch[] bluetoothMatches;

    public JSONObject toJSON() throws JSONException {
        JSONObject matchMessage = new JSONObject();

        if (bluetoothMatches != null) {
            JSONArray arr = new JSONArray();
            for (BluetoothMatch bmatch : bluetoothMatches) {
                arr.put(bmatch.toJson());
            }
            matchMessage.put("bluetoothMatches", arr);
        }

        if (areaMatches != null) {
            JSONArray arr2 = new JSONArray();
            for (AreaMatch amatch : areaMatches) {
                arr2.put(amatch.toJson());
            }
            matchMessage.put("areaMatches", arr2);
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
        if (obj.has("bluetoothMatches")) {
            JSONArray arr = obj.getJSONArray("bluetoothMatches");
            matchMessage.bluetoothMatches = new BluetoothMatch[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                matchMessage.bluetoothMatches[i] = BluetoothMatch.parse(arr.getJSONObject(i));
            }
        }
        return matchMessage;
    }

    public static String toHttpString() {
        return NetworkConstant.BASE_URL+"Messages/AreaReport";
    }
}
