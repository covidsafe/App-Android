package com.example.covidsafe.ble;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonObject;

@Entity(tableName = "ble_record_table")
public class BleRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "uploaded")
    private boolean uploaded;

    // may not be needed
    @NonNull
    @ColumnInfo(name = "infected")
    private boolean infected;

    public BleRecord(@NonNull String ss) {
        String[] elts = ss.split(",");
        this.id = elts[0];
        this.ts = Long.parseLong(elts[1]);
        this.uploaded = Boolean.parseBoolean(elts[2]);
        this.infected = Boolean.parseBoolean(elts[3]);
    }

    public BleRecord(@NonNull String id, long ts, boolean uploaded, boolean infected) {
        this.id = id;
        this.ts = ts;
        this.uploaded = uploaded;
        this.infected = infected;
    }

    public String getId() { return this.id; }

    public long getTs() {
        return this.ts;
    }

    public boolean getUploaded() { return this.uploaded; }

    public boolean getInfected() { return this.infected; }

    public String toString() {
        return this.id+","+this.ts+","+this.uploaded+","+this.infected;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",id);
        obj.addProperty("ts",ts);
        return obj;
    }
}
