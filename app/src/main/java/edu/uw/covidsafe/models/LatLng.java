package edu.uw.covidsafe.models;

public class LatLng {

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    private double latitude;
    private double longitude;
    public LatLng(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}