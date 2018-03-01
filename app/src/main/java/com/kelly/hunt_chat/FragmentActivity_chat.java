package com.kelly.hunt_chat;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tabbed_chat, container, false);

        chatRcv = (RecyclerView) view.findViewById(R.id.chat_menu_rcv);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_chat));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));

        //Friend List Handler
        LinearLayoutManager flLayoutManager = new LinearLayoutManager(getActivity());
        chatRcv.setLayoutManager(flLayoutManager);

        Query queryChat = databaseReference.orderByKey();

//        chatAdapter = new FirebaseRecyclerAdapter<ChatObj, ChatMenuItemHolder>(
//                ChatObj.class, R.layout.chat_menu_item, ChatMenuItemHolder.class, queryChat
//        ) {
//            @Override
//            protected void populateViewHolder(final ChatMenuItemHolder viewHolder, ChatObj model, int position) {
//                final DatabaseReference curRef = getRef(position);
//                final String chatId = curRef.getKey();
//
//                if(model.getType().equals(getString(R.string.chat_type_chat))){
//
//                    databaseReference.child(getString(R.string.firebase_chat_participant)).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Log.i(TAG, dataSnapshot.getValue().toString());
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            viewHolder.display_name.setText("");
//                        }
//                    });
//
////                    storageReference.child(friendID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
////                        @Override
////                        public void onSuccess(Uri uri) {
////                            Glide.with(getActivity())
////                                    .using(new FirebaseImageLoader())
////                                    .load(storageReference.child(friendID))
////                                    .into(viewHolder.image);
////                        }
////                    });
//                } else {
//                    if(model.getType().equals(getString(R.string.chat_type_game))){
//                        Toast.makeText(getActivity(), "Type 2", Toast.LENGTH_SHORT).show();
//                    }
//                }
//
//                //ToDo add onclick to chat
//                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(getActivity(), "Direct to Chat later", Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        };

        return view;
    }
}
