package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueToothSeed {

    String seed;
    UTCTime sequence_start_time;

    public static BlueToothSeed parse(JSONObject obj) throws JSONException {
        BlueToothSeed bluetoothSeed = new BlueToothSeed();
        if (obj.has("seed")) {
            bluetoothSeed.seed = obj.getString("seed");
        }
        if (obj.has("sequence_start_time")) {
            bluetoothSeed.sequence_start_time = UTCTime.parse(obj.getJSONObject("sequence_start_time"));
        }
        return bluetoothSeed;
    }

    public static JsonObject toJson(String seed, long ts) {
        JsonObject bluetooth_seed = new JsonObject();
        bluetooth_seed.addProperty("seed", seed);
        bluetooth_seed.add("sequence_start_time", UTCTime.toJson(ts));
        return bluetooth_seed;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject bluetooth_seed = new JSONObject();
        bluetooth_seed.put("seed", seed);
        bluetooth_seed.put("sequence_start_time", sequence_start_time.toJson());
        return bluetooth_seed;
    }
}
