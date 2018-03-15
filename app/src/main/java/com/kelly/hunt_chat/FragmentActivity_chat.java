package com.kelly.hunt_chat;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by User on 11/14/2017.
 */

public class FragmentActivity_chat extends Fragment{

    final private String TAG = "ChatActivity";

    private RecyclerView chatRcv;
    private FirebaseRecyclerAdapter<ChatObj, ChatMenuItemHolder> chatAdapter;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceChat;
    private DatabaseReference databaseReferenceUser;
    private StorageReference storageReferenceChat;
    private StorageReference storageReferenceUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tabbed_chat, container, false);

        chatRcv = (RecyclerView) view.findViewById(R.id.chat_menu_rcv);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_chat));
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_user));
        storageReferenceChat = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));
        storageReferenceUser = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_user));

        //Friend List Handler
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        chatRcv.setLayoutManager(layoutManager);

        //Todo sort base on the latest update
        Query queryChat = databaseReferenceChat.orderByKey();

        chatAdapter = new FirebaseRecyclerAdapter<ChatObj, ChatMenuItemHolder>(
                ChatObj.class, R.layout.chat_menu_item, ChatMenuItemHolder.class, queryChat
        ) {
            @Override
            protected void populateViewHolder(final ChatMenuItemHolder viewHolder, final ChatObj model, int position) {
                final DatabaseReference curRef = getRef(position);
                final String chatId = curRef.getKey();

                databaseReferenceChat.child(chatId).child(getString(R.string.firebase_chat_participant)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean found = false;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ChatPartiObj parti = snapshot.getValue(ChatPartiObj.class);

                            if(parti.getId().equals(user.getUid())){        //current user is in the parti lists and the view should be populated
                                found = true;
                                if(model.getType().equals(getString(R.string.chat_type_chat))){
                                    for (DataSnapshot innerSnap : dataSnapshot.getChildren()) {
                                        ChatPartiObj partiObj = innerSnap.getValue(ChatPartiObj.class);

                                        if(!partiObj.getId().equals(user.getUid())){
                                            viewHolder.display_name.setText(partiObj.getId());
                                            final String partiId = partiObj.getId();

                                            databaseReferenceUser.child(partiId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    UserInformation friend = dataSnapshot.getValue(UserInformation.class);
                                                    viewHolder.display_name.setText(friend.getName());
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                            storageReferenceUser.child(partiId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    Glide.with(getActivity())
                                                            .using(new FirebaseImageLoader())
                                                            .load(storageReferenceUser.child(partiId))
                                                            .into(viewHolder.image);
                                                }
                                            });

                                            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    Intent intent = new Intent(getActivity(), Chat_room.class);
                                                    Bundle bundle = new Bundle();
                                                    bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_chat));
                                                    bundle.putString(getString(R.string.chat_pass_id), partiId);
                                                    intent.putExtras(bundle);
                                                    startActivity(intent);
                                                }
                                            });
                                        }
                                    }
                                } else {
                                    if(model.getType().equals(getString(R.string.chat_type_game))){
                                        viewHolder.display_name.setText(model.getTitle());

                                        storageReferenceChat.child(chatId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(getActivity())
                                                        .using(new FirebaseImageLoader())
                                                        .load(storageReferenceChat.child(chatId))
                                                        .into(viewHolder.image);
                                            }
                                        });

                                        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Intent intent = new Intent(getActivity(), Chat_room.class);
                                                Bundle bundle = new Bundle();
                                                bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_game));
                                                bundle.putString(getString(R.string.chat_pass_id), chatId);
                                                intent.putExtras(bundle);
                                                startActivity(intent);
                                            }
                                        });
                                    }
                                }

                            }
                        }

                        if(!found){
                            viewHolder.layout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        chatRcv.setAdapter(chatAdapter);

        return view;
    }
}
