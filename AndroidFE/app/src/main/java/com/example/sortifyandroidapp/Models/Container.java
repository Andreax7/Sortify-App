package com.example.sortifyandroidapp.Models;

public class Container {

    Integer containerId;
    Integer typeId;
    Integer active;
    double longitude;
    double latitude;

    public Container(Integer typeId, double longitude, double latitude) {
        this.typeId = typeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Container(Integer active) {
        this.active = active;
    }

    public Container(Integer containerId, Integer typeId, Integer active, double longitude, double latitude) {
        this.containerId = containerId;
        this.typeId = typeId;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
    }


}
