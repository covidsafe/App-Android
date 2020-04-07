package edu.uw.covidsafe.gps;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gps_record_table")
public class GpsRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "lat")
    private double lat;

    @NonNull
    @ColumnInfo(name = "longi")
    private double longi;

    @NonNull
    @ColumnInfo(name = "provider")
    private String provider;

    public GpsRecord(@NonNull long ts, double lat, double longi, String provider) {
        this.ts = ts;
        this.lat = lat;
        this.longi = longi;
        this.provider = provider;
    }

    public GpsRecord(@NonNull String ss) {
        String[] elts = ss.split(",");
        this.ts = Long.parseLong(elts[0]);
        this.lat = Double.parseDouble(elts[1]);
        this.longi = Double.parseDouble(elts[2]);
        this.provider = elts[3];
    }

    public long getTs() {
        return this.ts;
    }

    public double getLat() { return this.lat; }

    public double getLongi() { return this.longi; }

    public String getProvider() { return this.provider; }

    public String toString() {
        return this.ts+","+this.lat+","+this.longi+","+this.provider;
    }

}
