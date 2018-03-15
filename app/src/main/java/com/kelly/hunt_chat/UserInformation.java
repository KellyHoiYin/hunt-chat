package com.kelly.hunt_chat;

import android.app.Activity;

/**
 * Created by User on 10/31/2017.
 */

public class UserInformation {
    private String name;
    private String username;
    private boolean push;
    private boolean addUsername;
    private double lat;
    private double lon;

    public UserInformation(){}

    public UserInformation(String name, String username, boolean push, boolean addUsername) {
        this.name = name;
        this.username = username;
        this.push = push;
        this.addUsername = addUsername;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }

    public boolean isAddUsername() { return addUsername; }

    public void setAddUsername(boolean addUsername) { this.addUsername = addUsername; }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
