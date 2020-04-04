package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import edu.uw.covidsafe.comms.NetworkConstant;

public class AnnounceRequest {
    MatchMessage match_criteria;
    Region region;

    public static JSONObject toJson(String[] seed, long[] ts,
                                    double lat, double longi,
                                    int precision) throws JSONException {
        JSONObject announceRequest = new JSONObject();
        JSONObject matchMessage = MatchMessage.toJson(seed, ts);
        JSONObject region = Region.toJson(lat, longi, precision);
        announceRequest.put("match_message", matchMessage);
        announceRequest.put("region", region);
        return announceRequest;
    }

    public static String toHttpString() {
        return NetworkConstant.BASE_URL+"Message/";
    }
}
