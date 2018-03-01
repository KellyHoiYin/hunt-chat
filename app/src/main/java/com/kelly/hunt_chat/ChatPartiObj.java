package com.kelly.hunt_chat;

/**
 * Created by User on 2/28/2018.
 */

public class ChatPartiObj {

    private String id;
    private String role;

    public ChatPartiObj() {
    }

    public ChatPartiObj(String id, String role) {
        this.id = id;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
