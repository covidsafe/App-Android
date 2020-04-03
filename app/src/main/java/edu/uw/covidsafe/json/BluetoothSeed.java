package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class BluetoothSeed {

    String seed;
    UTCTime sequence_start_time;

    public static BluetoothSeed parse(JsonObject obj) {
        BluetoothSeed bluetoothSeed = new BluetoothSeed();
        if (obj.has("seed")) {
            bluetoothSeed.seed = obj.get("seed").getAsString();
        }
        if (obj.has("sequence_start_time")) {
            bluetoothSeed.sequence_start_time = UTCTime.parse(obj.get("sequence_start_time").getAsJsonObject());
        }
        return bluetoothSeed;
    }

    public static JsonObject toJson(String seed, long ts) {
        JsonObject bluetooth_seed = new JsonObject();
        bluetooth_seed.addProperty("seed", seed);
        bluetooth_seed.add("sequence_start_time", UTCTime.toJson(ts));
        return bluetooth_seed;
    }

    public JsonObject toJson() {
        JsonObject bluetooth_seed = new JsonObject();
        bluetooth_seed.addProperty("seed", seed);
        bluetooth_seed.add("sequence_start_time", sequence_start_time.toJson());
        return bluetooth_seed;
    }
}
