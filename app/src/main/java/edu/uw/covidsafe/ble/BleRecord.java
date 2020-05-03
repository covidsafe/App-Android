package edu.uw.covidsafe.ble;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

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

    @NonNull
    @ColumnInfo(name = "model")
    private int model;

    public BleRecord(@NonNull String ss) {
        String[] elts = ss.split(",");
        this.uuid = elts[0];
        this.ts = Long.parseLong(elts[1]);
        this.rssi = Integer.parseInt(elts[2]);
        this.model = Integer.parseInt(elts[3]);
    }

    public BleRecord(@NonNull String uuid, long ts, int rssi, int model) {
        this.uuid = uuid;
        this.ts = ts;
        this.rssi = rssi;
        this.model = model;
    }

    public String getUuid() { return this.uuid; }

    public long getTs() {
        return this.ts;
    }

    public int getRssi() { return this.rssi; }

    public int getModel() {
        return model;
    }

    public void setModel(int model) {
        this.model = model;
    }

    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date(this.ts);
        return this.uuid +","+sdf.format(d)+","+this.rssi;
    }
}
