package com.kelly.hunt_chat;

import android.content.res.Resources;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by User on 1/29/2018.
 */

public class FriendRequestItemHolder extends RecyclerView.ViewHolder {
    public ImageView image;
    public TextView display_name;
    public TextView user_id;
    public TextView date;
    public ImageButton button_confirm;
    public ImageButton button_reject;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public FriendRequestItemHolder(View viewitem){
        super(viewitem);

        image = (ImageView) itemView.findViewById(R.id.friend_req_image);
        display_name = (TextView) itemView.findViewById(R.id.friend_req_name);
        user_id = (TextView) itemView.findViewById(R.id.friend_req_id);
        date = (TextView) itemView.findViewById(R.id.friend_req_date);
        button_confirm = (ImageButton) itemView.findViewById(R.id.friend_req_button_confirm);
        button_reject = (ImageButton) itemView.findViewById(R.id.friend_req_button_reject);
    }
}
