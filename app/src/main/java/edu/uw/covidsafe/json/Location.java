package edu.uw.covidsafe.json;

import com.google.gson.JsonObject;

public class Location {
    public double latitude;
    public double longitude;

    public static Location parse(JsonObject obj) {
        Location loc = new Location();
        if (obj.has("latitude")) {
            loc.latitude = obj.get("latitude").getAsDouble();
        }
        if (obj.has("longitude")) {
            loc.longitude = obj.get("longitude").getAsDouble();
        }
        return loc;
    }
}
