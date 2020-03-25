package com.example.covidsafe.models;

import com.google.gson.JsonObject;

public class BleRecord {
    public long ts;
    public String address;
    public int rssi;

    public BleRecord(String ss) {
        String[] elts = ss.split(",");
        this.ts = Long.parseLong(elts[0]);
        this.address = elts[1];
        this.rssi = Integer.parseInt(elts[2]);
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("timestamp",ts);
        obj.addProperty("address",address);
        obj.addProperty("rssi",rssi);
        return obj;
    }
}
