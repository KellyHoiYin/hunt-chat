package com.kelly.hunt_chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

public class Chat_room extends AppCompatActivity implements View.OnClickListener{

    final private String TAG = "ChatRoom";

    private RecyclerView cMsgRcv;
//    private FirebaseRecyclerAdapter<ChatObj, FriendRequestItemHolder> cMsgAdapter;
    private EditText input_text;
    private Button send_button;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference partiRef;
    private DatabaseReference userRef;
    private StorageReference storageReference;

    private String chat_type;
    private String chat_id;
    private boolean recordExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Chat Room");

        chat_type = getIntent().getExtras().getString(getString(R.string.chat_pass_type));
        chat_id = getIntent().getExtras().getString(getString(R.string.chat_pass_id));

        cMsgRcv = (RecyclerView) findViewById(R.id.chat_msg_rcv);
        input_text = (EditText) findViewById(R.id.chat_input_text);
        send_button = (Button) findViewById(R.id.chat_send_button);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_chat));
        userRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_user));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));

        recordExist = false;

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
                        //setting the chat name
                        if(chat_type.equals(getString(R.string.chat_type_chat))){
                            //get friend's name
                            for(ChatPartiObj elem : cur.getPartis()){
                                if(!elem.getId().equals(user.getUid())){
                                    userRef.child(elem.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            UserInformation friend = dataSnapshot.getValue(UserInformation.class);
                                            setTitle(friend.getName());
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }
                        } else {
                            if(chat_type.equals(getString(R.string.chat_type_game))){
                                setTitle(cur.getTitle());
                            }
                        }

                        final String key = snapshot.getKey();
                        //Todo Display the chat history
                        break;
                    } else {
                        //create a new chat when there is no record of this user with the friend conversation
                        if(chat_type.equals(getString(R.string.chat_type_chat))) {
                            createNewChat();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });

        send_button.setOnClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                Intent intent = new Intent(getApplicationContext(), TabbedActivity.class);
                final Bundle bundle = new Bundle();
                bundle.putString("TabNumber", "1");
                intent.putExtras(bundle);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void createNewChat(){
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

    //Todo enter a new message to the chat
    @Override
    public void onClick(View v){
        if(v == send_button){
            Toast.makeText(this, input_text.getText(), Toast.LENGTH_SHORT).show();
        }
    }
}