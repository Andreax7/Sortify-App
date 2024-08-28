package com.example.sortifyandroidapp.Models;

public class ContainerLocation {
    private double latitude;
    private double longitude;
    private String trashType;

    public ContainerLocation(double latitude, double longitude, String trashType) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.trashType = trashType;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTrashType() {
        return trashType;
    }

    public void setTrashType(String trashType) {
        this.trashType = trashType;
    }
// Getters and setters
}