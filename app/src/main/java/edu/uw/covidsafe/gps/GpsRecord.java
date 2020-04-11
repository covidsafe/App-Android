package edu.uw.covidsafe.gps;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import edu.uw.covidsafe.utils.CryptoUtils;

@Entity(tableName = "gps_record_table")
public class GpsRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts")
    private long ts;

    @NonNull
    @ColumnInfo(name = "lat")
    private String latEncrypted;

    @NonNull
    @ColumnInfo(name = "longi")
    private String longiEncrypted;

    @NonNull
    @ColumnInfo(name = "provider")
    private String provider;

    public GpsRecord(@NonNull long ts, String latEncrypted, String longiEncrypted, String provider) {
        this.ts = ts;
        setLat(Double.parseDouble(latEncrypted));
        setLongi(Double.parseDouble(latEncrypted));
        this.provider = provider;
    }

    public GpsRecord(@NonNull long ts, double lat, double longi, String provider) {
        this.ts = ts;
        setLat(lat);
        setLongi(longi);
        this.provider = provider;
    }

    public GpsRecord(@NonNull String ss) {
        String[] elts = ss.split(",");
        this.ts = Long.parseLong(elts[0]);
//        setLat(Double.parseDouble(elts[1]));
//        setLongi(Double.parseDouble(elts[2]));
        this.provider = elts[3];
    }

    public long getTs() {
        return this.ts;
    }

    public String getLatEncrypted() { return this.latEncrypted; }

    public double getLat() { return Double.parseDouble(CryptoUtils.decrypt(this.latEncrypted+"")); }

    public void setLat(double lat) { this.latEncrypted = CryptoUtils.encrypt(lat+""); }

    public String getLongiEncrypted() { return this.longiEncrypted; }

    public double getLongi() { return Double.parseDouble(CryptoUtils.decrypt(this.longiEncrypted+"")); }

    public void setLongi(double longi) { this.longiEncrypted = CryptoUtils.encrypt(longi+""); }

    public String getProvider() { return this.provider; }

}
