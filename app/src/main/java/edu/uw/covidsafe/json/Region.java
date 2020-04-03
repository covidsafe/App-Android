package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class Region {
    public static JsonObject toJson(double lat, double longi, double precision) {
        JsonObject region = new JsonObject();
        region.addProperty("lattitude_prefix", lat);
        region.addProperty("longitude_prefix", longi);
        region.addProperty("precision", precision);
        return region;
    }
}
