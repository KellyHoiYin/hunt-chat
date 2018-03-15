package com.kelly.hunt_chat;

import java.util.List;

/**
 * Created by User on 2/26/2018.
 */

public class ChatObj {

    private String type;
    private String owner;
    private boolean publ;
    private boolean authorised;
    private String title;
    private double location_lad;
    private double location_long;
    private String status;
    private String winner;

    private List<ChatPartiObj> partis;

    public ChatObj() {}

//    public ChatObj(String type, String owner, boolean publ, boolean authorised, String title, String location, String status, String winner) {
//        this.type = type;
//        this.owner = owner;
//        this.publ = publ;
//        this.authorised = authorised;
//        this.title = title;
//        this.location = location;
//        this.status = status;
//        this.winner = winner;
//    }
//
//

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public boolean isPubl() {
        return publ;
    }

    public void setPubl(boolean publ) {
        this.publ = publ;
    }

    public boolean isAuthorised() {
        return authorised;
    }

    public void setAuthorised(boolean authorised) {
        this.authorised = authorised;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getLocation_lad() {
        return location_lad;
    }

    public void setLocation_lad(double location_lad) {
        this.location_lad = location_lad;
    }

    public double getLocation_long() {
        return location_long;
    }

    public void setLocation_long(double location_long) {
        this.location_long = location_long;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public List<ChatPartiObj> getPartis() {
        return partis;
    }

    public void setPartis(List<ChatPartiObj> partis) {
        this.partis = partis;
    }
}
