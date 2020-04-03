package edu.uw.covidsafe.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class BluetoothMatch {
    public BluetoothSeed[] seeds;

    public static BluetoothMatch parse(JsonObject obj) {
        BluetoothMatch bluetoothMatch = new BluetoothMatch();
        if (obj.has("seeds")) {
            JsonArray array = obj.get("seeds").getAsJsonArray();
            bluetoothMatch.seeds = new BluetoothSeed[array.size()];
            for (int i = 0; i < array.size(); i++) {
                bluetoothMatch.seeds[i] = BluetoothSeed.parse(array.get(i).getAsJsonObject());
            }
        }
        return bluetoothMatch;
    }

    public JsonObject toJson() {
        JsonObject bluetooth_match = new JsonObject();
        JsonArray arr = new JsonArray();
        for (BluetoothSeed seed : seeds) {
            arr.add(seed.toJson());
        }
        bluetooth_match.add("seeds", arr);
        return bluetooth_match;
    }
}
