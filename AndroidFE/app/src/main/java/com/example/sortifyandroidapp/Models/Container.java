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

    public Container(Integer containerId, Integer typeId, Integer active, double longitude, double latitude) {
        this.containerId = containerId;
        this.typeId = typeId;
        this.active = active;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Integer getContainerId() {
        return containerId;
    }

    public void setContainerId(Integer containerId) {
        this.containerId = containerId;
    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

}
