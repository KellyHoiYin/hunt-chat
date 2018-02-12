package com.kelly.hunt_chat;

/**
 * Created by User on 2/3/2018.
 */

public class FriendObj {

    private String fl_id;
    private String fl_added_date;

    public FriendObj() {}

    public FriendObj(String fl_id, String fl_added_date) {
        this.fl_id = fl_id;
        this.fl_added_date = fl_added_date;
    }

    public String getFl_id() {
        return fl_id;
    }

    public void setFl_id(String fl_id) {
        this.fl_id = fl_id;
    }

    public String getFl_added_date() {
        return fl_added_date;
    }

    public void setFl_added_date(String fl_added_date) {
        this.fl_added_date = fl_added_date;
    }
}
