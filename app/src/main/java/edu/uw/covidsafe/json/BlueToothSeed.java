package edu.uw.covidsafe.json;

import org.json.JSONException;
import org.json.JSONObject;

public class BlueToothSeed {

    public String seed;
    public long sequenceStartTime;
    public long sequenceEndTime;

    public static BlueToothSeed parse(JSONObject obj) throws JSONException {
        BlueToothSeed bluetoothSeed = new BlueToothSeed();
        if (obj.has("seed")) {
            bluetoothSeed.seed = obj.getString("seed");
        }
        if (obj.has("sequenceStartTime")) {
            bluetoothSeed.sequenceStartTime = obj.getLong("sequenceStartTime");
        }
        if (obj.has("sequenceEndTime")) {
            bluetoothSeed.sequenceEndTime = obj.getLong("sequenceEndTime");
        }
        return bluetoothSeed;
    }

    public static JSONObject toJson(String seed, long ts_start, long ts_end) throws JSONException {
        JSONObject bluetooth_seed = new JSONObject();
        bluetooth_seed.put("seed", seed);
        bluetooth_seed.put("sequenceStartTime", ts_start);
        bluetooth_seed.put("sequenceEndTime", ts_end);
        return bluetooth_seed;
    }

    public JSONObject toJson() throws JSONException {
        JSONObject bluetooth_seed = new JSONObject();
        bluetooth_seed.put("seed", seed);
        bluetooth_seed.put("sequenceStartTime", sequenceStartTime);
        bluetooth_seed.put("sequenceEndTime", sequenceEndTime);
        return bluetooth_seed;
    }
}
