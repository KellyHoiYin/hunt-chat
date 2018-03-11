package com.kelly.hunt_chat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by User on 1/29/2018.
 */

public class ChatMessageItemHolder extends RecyclerView.ViewHolder {
    public TextView display_name;
    public RelativeLayout layout;
    public RelativeLayout inner_layout;
    public TextView message;

    public ChatMessageItemHolder(View viewitem){
        super(viewitem);

        display_name = (TextView) itemView.findViewById(R.id.chat_msg_name);
        message = (TextView) itemView.findViewById(R.id.chat_msg_msg);
        layout = (RelativeLayout) itemView.findViewById(R.id.chat_msg_layout);
        inner_layout = (RelativeLayout) itemView.findViewById(R.id.chat_msg_inner_layout);
    }
}
