package edu.uw.covidsafe.ble;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    @ColumnInfo(name = "rssi")
    private int rssi;

    public BleRecord(@NonNull String ss) {
        String[] elts = ss.split(",");
        this.id = elts[0];
        this.ts = Long.parseLong(elts[1]);
        this.rssi = Integer.parseInt(elts[2]);
    }

    public BleRecord(@NonNull String id, long ts, int rssi) {
        this.id = id;
        this.ts = ts;
        this.rssi = rssi;
    }

    public String getId() { return this.id; }

    public long getTs() {
        return this.ts;
    }

    public int getRssi() { return this.rssi; }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(this.ts);
        return this.id+","+sdf.format(d)+","+this.rssi;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id",id);
        obj.addProperty("ts",ts);
        return obj;
    }
}
