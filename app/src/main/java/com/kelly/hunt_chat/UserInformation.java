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
}
