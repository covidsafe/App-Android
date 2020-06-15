package edu.uw.covidsafe.gps;


import java.sql.Timestamp;

public class LocationData {

    private double latitude;
    private double longitude;
    private double time;

    public LocationData(Double latitude, Double longitude, double time) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.time = time;
    }

    public LocationData() {

    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
