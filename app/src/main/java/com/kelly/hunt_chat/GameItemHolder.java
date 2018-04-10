package com.kelly.hunt_chat;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by User on 3/15/2018.
 */

public class GameItemHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView display_name;
    public Button button_join;
    public LinearLayout layout;

    public GameItemHolder(View viewitem){
        super(viewitem);

        image = (ImageView) itemView.findViewById(R.id.game_list_image);
        display_name = (TextView) itemView.findViewById(R.id.game_list_name);
        button_join = (Button) itemView.findViewById(R.id.game_btn_join);
        layout = (LinearLayout) itemView.findViewById(R.id.game_list_item_layout);
    }
}
