package com.kelly.hunt_chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by User on 1/29/2018.
 */

public class ChatMenuItemHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView display_name;
    public RelativeLayout layout;
    public TextView last_message;

    public ChatMenuItemHolder(View viewitem){
        super(viewitem);

        image = (ImageView) itemView.findViewById(R.id.chat_menu_image);
        display_name = (TextView) itemView.findViewById(R.id.chat_menu_name);
        last_message = (TextView) itemView.findViewById(R.id.chat_menu_msg);
        layout = (RelativeLayout) itemView.findViewById(R.id.chat_menu_layout);
    }
}
