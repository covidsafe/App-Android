package edu.uw.covidsafe.gps;

import android.content.Context;
import android.util.Log;

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

//        Log.e("gps",ts+","+latEncrypted+","+longiEncrypted+","+provider);

        if (latEncrypted.length() > 0) {
            if (latEncrypted.charAt(latEncrypted.length()-1) == '\n') {
                this.latEncrypted = latEncrypted;
            }
            else {
                setLat(Double.parseDouble(latEncrypted));
            }
        }

        if (longiEncrypted.length() > 0) {
            if (longiEncrypted.charAt(longiEncrypted.length() - 1) == '\n') {
                this.longiEncrypted = longiEncrypted;
            } else {
                setLongi(Double.parseDouble(longiEncrypted));
            }
        }

        this.provider = provider;
    }

    public GpsRecord(@NonNull long ts, double lat, double longi, String provider) {
        this.ts = ts;
        setLat(lat);
        setLongi(longi);
        this.provider = provider;
    }

    public long getTs() {
        return this.ts;
    }

    public String getLatEncrypted() { return this.latEncrypted; }

    public double getLat() { return Double.parseDouble(CryptoUtils.decrypt(this.latEncrypted+"")); }

    public void setLat(double lat) {
        String s = CryptoUtils.encrypt(lat+"");
//        Log.e("gps","setting encrypted lat "+lat+"=>"+s);
        this.latEncrypted = s;
    }

    public String getLongiEncrypted() { return this.longiEncrypted; }

    public double getLongi() { return Double.parseDouble(CryptoUtils.decrypt(this.longiEncrypted+"")); }

    public void setLongi(double longi) {
        String s = CryptoUtils.encrypt(longi+"");
//        Log.e("gps","setting encrypted longi "+longi+"=>"+s);
        this.longiEncrypted = s;
    }

    public String getProvider() { return this.provider; }

}
