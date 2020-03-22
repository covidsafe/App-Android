package com.example.corona;

public class BlacklistRecord {
    double lat;
    double longi;
    String address;

    public BlacklistRecord(String ss) {
        String[] elts = ss.split(",");
        this.lat = Double.parseDouble(elts[0]);
        this.longi = Double.parseDouble(elts[1]);
        this.address = "";
        for(int i = 2; i < elts.length; i++) {
            this.address += elts[i];
        }
    }
}
