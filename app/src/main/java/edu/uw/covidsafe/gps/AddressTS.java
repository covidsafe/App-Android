package edu.uw.covidsafe.gps;

public class AddressTS {
    private String name = "";
    private String address;
    private double timestamp;

    AddressTS(String name, String address, double timestamp) {
        this.name = name;
        this.address = address;
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }
}
