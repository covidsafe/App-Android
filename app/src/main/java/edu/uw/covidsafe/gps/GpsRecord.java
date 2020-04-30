package edu.uw.covidsafe.gps;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;

import edu.uw.covidsafe.utils.CryptoUtils;

@Entity(tableName = "gps_record_table")
public class GpsRecord {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ts_start")
    private long ts_start;

    public long getTs_end() {
        return ts_end;
    }

    public void setTs_end(long ts_end) {
        this.ts_end = ts_end;
    }

    @ColumnInfo(name = "ts_end")
    private long ts_end;

    @NonNull
    @ColumnInfo(name = "lat")
    private String latEncrypted;

    @NonNull
    @ColumnInfo(name = "longi")
    private String longiEncrypted;

    @NonNull
    @ColumnInfo(name = "provider")
    private String provider;

    @ColumnInfo(name = "address")
    private String addressEncrypted;

    public GpsRecord() {

    }

    public GpsRecord(@NonNull long ts_start, long ts_end, String latEncrypted, String longiEncrypted, String provider, String addressEncrypted) {
        // don't use
    }

    public GpsRecord(@NonNull long ts_start, String latEncrypted, String longiEncrypted, String provider, String addressEncrypted) {
        this.ts_start = ts_start;
        this.latEncrypted = latEncrypted;
        this.longiEncrypted = longiEncrypted;
        this.provider = provider;
        this.addressEncrypted = addressEncrypted;
    }

    // accepts plaintext inputs
    public GpsRecord(@NonNull long ts_start, String latEncrypted, String longiEncrypted, String provider, Context cxt) {
        this.ts_start = ts_start;

        if (latEncrypted.length() > 0) {
            if (latEncrypted.charAt(latEncrypted.length()-1) == '\n') {
                this.latEncrypted = latEncrypted;
            }
            else {
                setLat(Double.parseDouble(latEncrypted), cxt);
            }
        }

        if (longiEncrypted.length() > 0) {
            if (longiEncrypted.charAt(longiEncrypted.length() - 1) == '\n') {
                this.longiEncrypted = longiEncrypted;
            } else {
                setLongi(Double.parseDouble(longiEncrypted), cxt);
            }
        }

        this.provider = provider;
    }

    public GpsRecord(@NonNull long ts_start, double lat, double longi, String provider, Context cxt) {
        setTs_start(ts_start);
        setLat(lat, cxt);
        setLongi(longi, cxt);
        this.provider = provider;
    }

    ////////////////////////////////////////////////////////////////////////////////////////
    // get encrypted properties
    public long getTs_start() {
        return this.ts_start;
    }

    public String getRawLat() {
        return this.latEncrypted;
    }

    public String getLatEncrypted() {
        return this.latEncrypted;
    }

    public String getRawLongi() {
        return this.longiEncrypted;
    }

    public String getLongiEncrypted() {
        return this.longiEncrypted;
    }

    public String getProvider() { return this.provider; }

    public String getAddressEncrypted() { return this.addressEncrypted; }

    public String getRawAddress() { return this.addressEncrypted; }
    ////////////////////////////////////////////////////////////////////////////////////////
    // decrypts properties
    public double getLat(Context cxt) {
        return Double.parseDouble(CryptoUtils.decrypt(cxt, this.latEncrypted));
    }

    public double getLongi(Context cxt) {
        return Double.parseDouble(CryptoUtils.decrypt(cxt, this.longiEncrypted));
    }

    public String getAddress(Context cxt) { return CryptoUtils.decrypt(cxt, this.addressEncrypted); }
    ////////////////////////////////////////////////////////////////////////////////////////
    // sets and encrypts properties
    public void setLat(double lat, Context cxt) {
        String s = CryptoUtils.encrypt(cxt, lat+"");
        this.latEncrypted = s;
    }

    public void setLongi(double longi, Context cxt) {
        String s = CryptoUtils.encrypt(cxt, longi+"");
        this.longiEncrypted = s;
    }
    public void setAddress(String address, Context cxt) {
        this.addressEncrypted = CryptoUtils.encrypt(cxt, address);
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    // sets pre-encrypted properties
    public void setTs_start(long ts_start) {
        this.ts_start = ts_start;
    }

    public void setRawLat(String lat) {
        this.latEncrypted = lat;
    }

    public void setRawLongi(String lon) { this.longiEncrypted = lon; }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public void setRawAddress(String address) {
        this.addressEncrypted = address;
    }
    ////////////////////////////////////////////////////////////////////////////////////////
    public void setLatEncrypted(String lat) { this.latEncrypted = lat; }

    public void setLongiEncrypted(String lon) { this.longiEncrypted = lon; }

    public void setAddressEncrypted(String address) { this.addressEncrypted = address; }
    ////////////////////////////////////////////////////////////////////////////////////////
    public String toString() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd hh:mm");
        String ts = format.format(this.getTs_start());
        return ts+" - "+this.getRawAddress()+" ("+this.getRawLat()+", "+this.getRawLongi()+")\n";
    }
}
