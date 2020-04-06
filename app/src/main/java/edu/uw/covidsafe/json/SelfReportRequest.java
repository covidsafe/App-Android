package edu.uw.covidsafe.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.comms.NetworkConstant;

public class SelfReportRequest {
    BlueToothSeed[] seeds;
    Region region;

    public static JSONObject toJson(String[] seeds, long[] ts_start, long[] ts_end, double lat, double longi, int precision) throws JSONException {
        JSONObject obj = new JSONObject();
        JSONArray arr = new JSONArray();
        for (int i = 0; i < seeds.length; i++) {
            arr.put(BlueToothSeed.toJson(seeds[i],ts_start[i],ts_end[i]));
        }
        obj.put("seeds",arr);
        obj.put("region",Region.toJson(lat,longi,precision));
        return obj;
    }

    public static String toHttpString() {
        return NetworkConstant.BASE_URL+"Messages/SeedReport";
    }
}
