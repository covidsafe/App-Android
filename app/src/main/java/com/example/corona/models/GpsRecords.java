package com.example.corona.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

public class GpsRecords {
    LinkedList<GpsRecord> records;

    public GpsRecords() {
        this.records = new LinkedList<>();
    }

    public GpsRecords(LinkedList<GpsRecord> records) {
        this.records = records;
    }

    public void add(GpsRecord record) {
        records.add(record);
    }

    public void addAll(List<GpsRecord> recs) {
        for (GpsRecord rec : recs) {
            records.add(rec);
        }
    }

    public JsonObject toJson() {
        JsonArray arr = new JsonArray();
        for (GpsRecord rec : records) {
            arr.add(rec.toJson());
        }
        JsonObject obj = new JsonObject();
        obj.add("data", arr);
        return obj;
    }
}
