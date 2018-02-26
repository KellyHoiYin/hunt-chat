package com.kelly.hunt_chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by User on 1/29/2018.
 */

public class FriendListItemHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView display_name;
    public RelativeLayout layout;

    public FriendListItemHolder(View viewitem){
        super(viewitem);

        image = (ImageView) itemView.findViewById(R.id.friend_list_image);
        display_name = (TextView) itemView.findViewById(R.id.friend_list_name);
        layout = (RelativeLayout) itemView.findViewById(R.id.friend_list_item_layout);
    }
}
