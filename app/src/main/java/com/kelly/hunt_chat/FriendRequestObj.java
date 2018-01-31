package com.kelly.hunt_chat;

/**
 * Created by User on 1/28/2018.
 */

public class FriendRequestObj {

    private String friends_req_id;
    private boolean seen;
    private String datetime;

    public FriendRequestObj(){}

    public FriendRequestObj(String friends_req_id, boolean seen, String datetime) {
        this.friends_req_id = friends_req_id;
        this.seen = seen;
        this.datetime = datetime;
    }

    public String getFriends_req_id() {
        return friends_req_id;
    }

    public void setFriends_req_id(String friends_req_id) {
        this.friends_req_id = friends_req_id;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
