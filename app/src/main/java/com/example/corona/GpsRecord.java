package com.example.corona;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class GpsRecord {
    long ts;
    double lat;
    double longi;

    public GpsRecord(String ss) {
        String[] elts = ss.split(",");
        this.ts = Long.parseLong(elts[0]);
        this.lat = Double.parseDouble(elts[1]);
        this.longi = Double.parseDouble(elts[2]);
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("timestamp",ts);

        JsonArray arr = new JsonArray();
        arr.add(lat);
        arr.add(longi);

        JsonObject geometry = new JsonObject();
        geometry.addProperty("type","Point");
        geometry.add("coordinates",arr);

        obj.add("geometry", geometry);

        return obj;
    }
}
