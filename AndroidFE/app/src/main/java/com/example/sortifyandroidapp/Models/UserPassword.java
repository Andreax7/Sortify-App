package com.example.sortifyandroidapp.Models;

/** **************************************************************************************
 *
 * This class model is used to change user password and to form a request send it to api */

public class UserPassword {
    private String password;

    public UserPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
