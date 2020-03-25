package com.example.covidsafe.models;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

public class BleRecords {
    public LinkedList<BleRecord> records;

    public BleRecords() {
        this.records = new LinkedList<>();
    }

    public BleRecords(LinkedList<BleRecord> records) {
        this.records = records;
    }

    public void add(BleRecord record) {
        records.add(record);
    }

    public void addAll(List<BleRecord> recs) {
        for (BleRecord rec : recs) {
            records.add(rec);
        }
    }

    public JsonObject toJson() {
        JsonArray arr = new JsonArray();
        for (BleRecord rec : records) {
            arr.add(rec.toJson());
        }
        JsonObject obj = new JsonObject();
        obj.add("data", arr);
        return obj;
    }
}
