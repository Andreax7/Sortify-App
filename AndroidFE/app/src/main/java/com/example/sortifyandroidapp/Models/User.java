package com.example.sortifyandroidapp.Models;

// modal class for storing users data
public class User {

    Integer userId;
    String email;
    String firstName;
    String lastName;
    String password;
    Integer role;
    Integer active;
    String picture;
    String token;


    // constructor to initialize the variables for registering user
    public User(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }

    // constructor to initialize the variables for logging user
    public User(String email, String password ) {
        this.email = email;
        this.password = password;
    }

    public User(String JWTtoken){
        this.token = JWTtoken;
    }

    /**
     * No args constructor for use in serialization
     */
    public User() {
    }


    /**
     * getter and setter methods
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }

    public Integer getActive() {
        return active;
    }

    public void setActive(Integer active) {
        this.active = active;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getToken() {
        return token;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
