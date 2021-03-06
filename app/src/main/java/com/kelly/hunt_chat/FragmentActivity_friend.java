package com.kelly.hunt_chat;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

/**
 * Created by Kelly on 11/14/2017.
 */

public class FragmentActivity_friend extends Fragment{

    final private String TAG = "FriendActivity";

    private RecyclerView frRcv;
    private FirebaseRecyclerAdapter<FriendRequestObj, FriendRequestItemHolder> frAdapter;

    private RecyclerView flRcv;
    private FirebaseRecyclerAdapter<FriendObj, FriendListItemHolder> flAdapter;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference friendListReference;
    private DatabaseReference friendReqReference;
    private StorageReference storageReference;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tabbed_friend, container, false);
        setHasOptionsMenu(true);

        frRcv = (RecyclerView) view.findViewById(R.id.friend_request_rcv);

        flRcv = (RecyclerView) view.findViewById(R.id.friend_list_rcv);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_user));
        friendReqReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user)).child(user.getUid()).child(getString(R.string.firebase_friend_requests));
        friendListReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user)).child(user.getUid()).child(getString(R.string.firebase_friend_list));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_user));

        //Friend List Handler
        LinearLayoutManager flLayoutManager = new LinearLayoutManager(getActivity());
        flRcv.setLayoutManager(flLayoutManager);

        Query query_friend_list = friendListReference.orderByKey();

        flAdapter = new FirebaseRecyclerAdapter<FriendObj, FriendListItemHolder>(
                FriendObj.class, R.layout.friend_list_item, FriendListItemHolder.class, query_friend_list
        ) {
            @Override
            protected void populateViewHolder(final FriendListItemHolder viewHolder, FriendObj model, int position) {
                final String friendID = model.getFl_id();

                databaseReference.child(friendID).child(getString(R.string.firebase_displayname)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.display_name.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        viewHolder.display_name.setText("");
                    }
                });

                storageReference.child(friendID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference.child(friendID))
                                .into(viewHolder.image);
                    }
                });

                viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), Chat_room.class);
                        Bundle bundle = new Bundle();
                        bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_chat));
                        bundle.putString(getString(R.string.chat_pass_id), friendID);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            }
        };

        flRcv.setAdapter(flAdapter);

        //Friend Request Handler
        LinearLayoutManager frLayoutManager = new LinearLayoutManager(getActivity());
        frLayoutManager.setReverseLayout(true);
        frLayoutManager.setStackFromEnd(true);
        frRcv.setLayoutManager(frLayoutManager);

        Query query_friend_req = friendReqReference.orderByKey();

        frAdapter = new FirebaseRecyclerAdapter<FriendRequestObj, FriendRequestItemHolder>(
                FriendRequestObj.class, R.layout.friend_request_item, FriendRequestItemHolder.class, query_friend_req
        ) {
            @Override
            protected void populateViewHolder(final FriendRequestItemHolder viewHolder, FriendRequestObj model, int position) {
                final String requesterID = model.getFriends_req_id();
                viewHolder.user_id.setText(requesterID);
                viewHolder.date.setText(model.getDatetime());

                databaseReference.child(requesterID).child(getString(R.string.firebase_displayname)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        viewHolder.display_name.setText(dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        viewHolder.display_name.setText("");
                    }
                });

                storageReference.child(requesterID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(getActivity())
                                .using(new FirebaseImageLoader())
                                .load(storageReference.child(requesterID))
                                .into(viewHolder.image);
                    }
                });

                viewHolder.button_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                        Calendar cal = Calendar.getInstance();

                        // add to this user friend list
                        FriendObj curUserFriendList = new FriendObj(requesterID, dateFormat.format(cal.getTime()));
                        databaseReference.child(user.getUid()).child(getString(R.string.firebase_friend_list))
                            .push().setValue(curUserFriendList);

                        // add to the requester friend list
                        FriendObj requesterFriendList = new FriendObj(user.getUid(), dateFormat.format(cal.getTime()));
                        databaseReference.child(requesterID).child(getString(R.string.firebase_friend_list))
                                .push().setValue(requesterFriendList);

                        // delete request on the signed user
                        databaseReference.child(user.getUid()).child(getString(R.string.firebase_friend_requests))
                                .orderByChild(getString(R.string.firebase_fr_id)).equalTo(requesterID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    databaseReference.child(user.getUid()).child(getString(R.string.firebase_friend_requests)).child(dataSnapshot.getValue().toString().split("=")[0].substring(1)).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage());
                            }
                        });

                        // delete request on the requester
                        databaseReference.child(requesterID).child(getString(R.string.firebase_friend_requests))
                                .orderByChild(getString(R.string.firebase_fr_id)).equalTo(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    databaseReference.child(requesterID).child(getString(R.string.firebase_friend_requests)).child(dataSnapshot.getValue().toString().split("=")[0].substring(1)).removeValue();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage());
                            }
                        });
                    }
                });

                viewHolder.button_reject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child(user.getUid()).child(getString(R.string.firebase_friend_requests))
                                .orderByChild(getString(R.string.firebase_fr_id)).equalTo(requesterID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                databaseReference.child(user.getUid()).child(getString(R.string.firebase_friend_requests)).child(dataSnapshot.getValue().toString().split("=")[0].substring(1)).removeValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.e(TAG, databaseError.getMessage());
                            }
                        });
                    }
                });

            }
        };

        frRcv.setAdapter(frAdapter);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_add_friends, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friends_add:
                startActivity(new Intent(getActivity(), Friends_add_new.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        frAdapter.cleanup();
    }
}
