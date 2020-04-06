package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueToothSeed {

    public String seed;
    public long sequence_start_time;
    public long sequence_end_time;

    public static BlueToothSeed parse(JSONObject obj) throws JSONException {
        BlueToothSeed bluetoothSeed = new BlueToothSeed();
        if (obj.has("seed")) {
            bluetoothSeed.seed = obj.getString("seed");
        }
        if (obj.has("sequence_start_time")) {
            bluetoothSeed.sequence_start_time = obj.getLong("sequence_start_time");
        }
        if (obj.has("sequence_end_time")) {
            bluetoothSeed.sequence_end_time = obj.getLong("sequence_end_time");
        }
        return bluetoothSeed;
    }

    public static JsonObject toJson(String seed, long ts_start, long ts_end) {
        JsonObject bluetooth_seed = new JsonObject();
        bluetooth_seed.addProperty("seed", seed);
        bluetooth_seed.addProperty("sequence_start_time", ts_start);
        bluetooth_seed.addProperty("sequence_end_time", ts_end);
        return bluetooth_seed;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject bluetooth_seed = new JSONObject();
        bluetooth_seed.put("seed", seed);
        bluetooth_seed.put("sequence_start_time", sequence_start_time);
        bluetooth_seed.put("sequence_end_time", sequence_end_time);
        return bluetooth_seed;
    }
}
