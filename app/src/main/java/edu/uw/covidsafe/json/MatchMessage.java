package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class MatchMessage {
    public String match_protocol_version;
    public AreaMatch area_match;
    public BluetoothMatch bluetooth_match;

    public static MatchMessage parse(JsonObject obj) {
        MatchMessage matchMessage = new MatchMessage();
        if (obj.has("match_protocol_version")) {
            matchMessage.match_protocol_version = obj.get("match_protocol_version").getAsString();
        }
        if (obj.has("area_match")) {
            matchMessage.area_match = AreaMatch.parse(obj.get("area_match").getAsJsonObject());
        }
        if (obj.has("bluetooth_match")) {
            matchMessage.bluetooth_match = BluetoothMatch.parse(obj.get("bluetooth_match").getAsJsonObject());
        }
        return matchMessage;
    }

    public static JsonObject toJson(String[] seed, long[] ts) {
        JsonObject matchMessage = new JsonObject();
        matchMessage.addProperty("match_protocol_version", 1);

        JsonArray arr = new JsonArray();
        for (int i = 0; i < seed.length; i++) {
            arr.add(BluetoothSeed.toJson(seed[i], ts[i]));
        }

        matchMessage.add("bluetooth_match", arr);
        return matchMessage;
    }
}
