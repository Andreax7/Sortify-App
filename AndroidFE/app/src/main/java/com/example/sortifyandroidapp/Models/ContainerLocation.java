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

    // Getters and setters
}