package com.example.corona;

public class GpsRecord {
    long ts;
    double lat;
    double longi;

    public GpsRecord(long ts, double lat, double longi) {
        this.ts = ts;
        this.lat = lat;
        this.longi = longi;
    }

    public GpsRecord(String ss) {
        String[] elts = ss.split(",");
        this.ts = Long.parseLong(elts[0]);
        this.lat = Double.parseDouble(elts[1]);
        this.longi = Double.parseDouble(elts[2]);
    }
}
