package com.example.sortifyandroidapp.Models;

public class UserAdapterDataModel {
    private String email;
    private String active;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public UserAdapterDataModel(String email, String active) {
        this.email = email;
        this.active = active;
    }
}
