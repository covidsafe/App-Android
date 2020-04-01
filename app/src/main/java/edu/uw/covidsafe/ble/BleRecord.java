package edu.uw.covidsafe.ble;

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
    @ColumnInfo(name = "uuid")
    private String uuid;

    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "rssi")
    private int rssi;

    public BleRecord(@NonNull String ss) {
        String[] elts = ss.split(",");
        this.uuid = elts[0];
        this.ts = Long.parseLong(elts[1]);
        this.rssi = Integer.parseInt(elts[2]);
    }

    public BleRecord(@NonNull String uuid, long ts, int rssi) {
        this.uuid = uuid;
        this.ts = ts;
        this.rssi = rssi;
    }

    public String getUuid() { return this.uuid; }

    public long getTs() {
        return this.ts;
    }

    public int getRssi() { return this.rssi; }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(this.ts);
        return this.uuid +","+sdf.format(d)+","+this.rssi;
    }

    public JsonObject toJson() {
        JsonObject obj = new JsonObject();
        obj.addProperty("id", uuid);
        obj.addProperty("ts",ts);
        return obj;
    }
}
