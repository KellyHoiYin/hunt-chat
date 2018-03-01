package com.kelly.hunt_chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class Chat_room extends AppCompatActivity {

    final private String TAG = "ChatRoom";

    private RecyclerView cMsgRcv;
//    private FirebaseRecyclerAdapter<ChatObj, FriendRequestItemHolder> cMsgAdapter;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference partiRef;
    private StorageReference storageReference;

    private String chat_type;
    private String chat_id;
    private boolean recordExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Kelly");

        chat_type = getIntent().getExtras().getString(getString(R.string.chat_pass_type));
        chat_id = getIntent().getExtras().getString(getString(R.string.chat_pass_id));

        cMsgRcv = (RecyclerView) findViewById(R.id.friend_request_rcv);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_chat));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));

        recordExist = false;

        //Todo Insert new chat record is there is no match
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ChatObj cur = snapshot.getValue(ChatObj.class);
                    if(chat_type.equals(getString(R.string.chat_type_chat))){       //friend chats
                        if(cur.getType().equals(chat_type)){
                            for(ChatPartiObj elem : cur.getPartis()){
                                if(!recordExist && elem.getId().equals(user.getUid())){
                                    for(ChatPartiObj e : cur.getPartis()){
                                        if(!recordExist && e.getId().equals(chat_id)){
                                            recordExist = true;
                                            break;
                                        }
                                    }
                                }
                            }

//                            if(recordExist)
                        }
                    } //Todo handle the request when it is a game

                    if(recordExist) {
                        final String key = snapshot.getKey();
                        //Todo Display the chat history
                        break;
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });

        //create a new chat when there is no record of this user with the friend conversation
        if(!recordExist && chat_type.equals(getString(R.string.chat_type_chat))){
            //insert a new chat room for this user and the friend ID
            ChatObj obj = new ChatObj();
            obj.setOwner(user.getUid());
            obj.setType(getString(R.string.chat_type_chat));

            List<ChatPartiObj> partis = new ArrayList<>();
            partis.add(new ChatPartiObj(user.getUid(), getString(R.string.chat_parti_host)));
            partis.add(new ChatPartiObj(chat_id,getString(R.string.chat_parti_friend)));

            obj.setPartis(partis);
            databaseReference.push().setValue(obj);
        }

        //Todo Else load the chat record

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                //Todo go to the specific tab
                startActivity(new Intent(getApplicationContext(), TabbedActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
