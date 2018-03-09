package com.kelly.hunt_chat;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Chat_room extends AppCompatActivity implements View.OnClickListener{

    final private String TAG = "ChatRoom";

    private RecyclerView cMsgRcv;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessageItemHolder> cMsgAdapter;

    private EditText input_text;
    private Button send_button;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference msgRef;
    private DatabaseReference userRef;
    private StorageReference storageReference;
    private StorageReference storageReferenceUser;

    private String chat_type;
    private String passed_id;
    private boolean recordExist;

    private String chat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Chat Room");

        chat_type = getIntent().getExtras().getString(getString(R.string.chat_pass_type));
        passed_id = getIntent().getExtras().getString(getString(R.string.chat_pass_id));

        if(chat_type.equals(getString(R.string.chat_type_game))){
            chat_id = passed_id;
        }

        cMsgRcv = (RecyclerView) findViewById(R.id.chat_msg_rcv);
        input_text = (EditText) findViewById(R.id.chat_input_text);
        send_button = (Button) findViewById(R.id.chat_send_button);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_chat));
        userRef = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_user));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));
        storageReferenceUser = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_user));

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
                                        if(!recordExist && e.getId().equals(passed_id)){
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
                        chat_id = snapshot.getKey();
                        msgRef = databaseReference.child(chat_id).child(getString(R.string.firebase_chat_message));

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

                        //Todo Display the chat history

                        LinearLayoutManager flLayoutManager = new LinearLayoutManager(getApplicationContext());
                        cMsgRcv.setLayoutManager(flLayoutManager);

                        Query query_msgs = msgRef.orderByKey();

                        cMsgAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageItemHolder>(
                                ChatMessage.class, R.layout.chat_msg_item, ChatMessageItemHolder.class, query_msgs
                        ) {
                            @Override
                            protected void populateViewHolder(final ChatMessageItemHolder viewHolder, ChatMessage model, int position) {
                                final String userId = model.getUser_id();

                                //this message is sent by this logged in user
                                if(userId.equals(user.getUid())){
                                    RelativeLayout.LayoutParams image_lp = (RelativeLayout.LayoutParams) viewHolder.image.getLayoutParams();
                                    image_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    viewHolder.image.setLayoutParams(image_lp);

                                    RelativeLayout.LayoutParams layout_lp = (RelativeLayout.LayoutParams) viewHolder.inner_layout.getLayoutParams();
                                    layout_lp.addRule(RelativeLayout.LEFT_OF, viewHolder.image.getId());
                                    viewHolder.inner_layout.setLayoutParams(layout_lp);

                                    viewHolder.message.setBackgroundResource(R.drawable.rounded_chat_right);
                                    RelativeLayout.LayoutParams msg_lp = (RelativeLayout.LayoutParams) viewHolder.message.getLayoutParams();
                                    msg_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    viewHolder.message.setLayoutParams(msg_lp);

                                    RelativeLayout.LayoutParams name_lp = (RelativeLayout.LayoutParams) viewHolder.display_name.getLayoutParams();
                                    name_lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    viewHolder.display_name.setLayoutParams(name_lp);
                                } else {
                                    RelativeLayout.LayoutParams layout_lp = (RelativeLayout.LayoutParams) viewHolder.inner_layout.getLayoutParams();
                                    layout_lp.addRule(RelativeLayout.RIGHT_OF, viewHolder.image.getId());
                                    viewHolder.inner_layout.setLayoutParams(layout_lp);
                                }

                                userRef.child(userId).child(getString(R.string.firebase_displayname)).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        viewHolder.display_name.setText(dataSnapshot.getValue().toString());
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        viewHolder.display_name.setText("");
                                    }
                                });

                                storageReferenceUser.child(userId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(storageReferenceUser.child(userId))
                                                .into(viewHolder.image);
                                    }
                                });

                                viewHolder.message.setText(model.getContent());
                                viewHolder.message.setPadding(20,6,20,6);
                            }
                        };

                        cMsgRcv.setAdapter(cMsgAdapter);

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
            partis.add(new ChatPartiObj(passed_id,getString(R.string.chat_parti_friend)));

            obj.setPartis(partis);
            databaseReference.push().setValue(obj);
    }

    //Todo enter a new message to the chat
    @Override
    public void onClick(View v){
        if(v == send_button){
            ChatMessage curMsg = new ChatMessage();
            curMsg.setContent(input_text.getText().toString());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Calendar cal = Calendar.getInstance();

            curMsg.setTimestamp(dateFormat.format(cal.getTime()));
            curMsg.setType(getString(R.string.chat_msg_normal));
            curMsg.setUser_id(user.getUid());

            msgRef.push().setValue(curMsg);
            input_text.setText(null);
        }
    }
}
