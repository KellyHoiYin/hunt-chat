package com.kelly.hunt_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 11/14/2017.
 */

public class FragmentActivity_game extends Fragment{

    final private String TAG = "GameActivity";

    private RecyclerView ngRcv;     //nearby games
    private FirebaseRecyclerAdapter<ChatObj, GameItemHolder> ngAdapter;

    private RecyclerView fgRcv;     //friends games
    private FirebaseRecyclerAdapter<ChatObj, GameItemHolder> fgAdapter;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceChat;
    private DatabaseReference databaseReferenceUser;
    private StorageReference storageReference;

    private double curLat;
    private double curLon;

    GPSTracker gps;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.tabbed_game, container, false);

        ngRcv = (RecyclerView) view.findViewById(R.id.nearby_game_rcv);

        fgRcv = (RecyclerView) view.findViewById(R.id.friend_game_rcv);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_chat));
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_user));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));

        //Todo disable the join button if the user is in the parti list
        //Nearby Games Handler
        LinearLayoutManager ngLayoutManager = new LinearLayoutManager(getActivity());
        ngRcv.setLayoutManager(ngLayoutManager);

        Query query_game = databaseReferenceChat.orderByKey();

        ngAdapter = new FirebaseRecyclerAdapter<ChatObj, GameItemHolder>(
                ChatObj.class, R.layout.game_list_item, GameItemHolder.class, query_game
        ) {
            @Override
            protected void populateViewHolder(final GameItemHolder viewHolder, final ChatObj model, int position) {
                final DatabaseReference curRef = getRef(position);
                final String chatId = curRef.getKey();

                ViewGroup.LayoutParams lp = viewHolder.layout.getLayoutParams();

                if(model.getType().equals(getString(R.string.chat_type_game))){
                    if(!model.isCompleted()){
                        if(isNear(model.getLocation_lad(), model.getLocation_long())){
                            //populate
                            viewHolder.display_name.setText(model.getTitle());

                            storageReference.child(chatId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(getActivity())
                                            .using(new FirebaseImageLoader())
                                            .load(storageReference.child(chatId))
                                            .into(viewHolder.image);
                                }
                            });

                            viewHolder.button_join.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(getActivity(), chatId, Toast.LENGTH_SHORT).show();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    LayoutInflater inflater = getActivity().getLayoutInflater();
                                    View view = inflater.inflate(R.layout.chat_join_option, null);
                                    builder.setTitle(getString(R.string.game_select_option));

                                    builder.setView(view);
                                    builder.show();

                                    ImageButton ib_hunt = (ImageButton) view.findViewById(R.id.join_btn_image_hunt);
                                    ib_hunt.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            List<ChatPartiObj> partis = model.getPartis();
                                            partis.add(new ChatPartiObj(user.getUid(), getString(R.string.chat_parti_hunter)));
                                            databaseReferenceChat.child(chatId).child(getString(R.string.firebase_chat_participant)).setValue(partis);

                                            Intent intent = new Intent(getActivity(), Chat_room.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_game));
                                            bundle.putString(getString(R.string.chat_pass_id), chatId);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });

                                    ImageButton ib_spec = (ImageButton) view.findViewById(R.id.join_btn_image_spec);
                                    ib_spec.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            List<ChatPartiObj> partis = model.getPartis();
                                            partis.add(new ChatPartiObj(user.getUid(), getString(R.string.chat_parti_analyser)));
                                            databaseReferenceChat.child(chatId).child(getString(R.string.firebase_chat_participant)).setValue(partis);

                                            Intent intent = new Intent(getActivity(), Chat_room.class);
                                            Bundle bundle = new Bundle();
                                            bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_game));
                                            bundle.putString(getString(R.string.chat_pass_id), chatId);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            });
                        } else {
                            lp.height = 0;
                            viewHolder.layout.setLayoutParams(lp);
                        }
                    } else {
                        lp.height = 0;
                        viewHolder.layout.setLayoutParams(lp);
                    }
                } else {
                    lp.height = 0;
                    viewHolder.layout.setLayoutParams(lp);
                }
            }
        };

        ngRcv.setAdapter(ngAdapter);

        //Friends game Handler
        LinearLayoutManager fgLayoutManager = new LinearLayoutManager(getActivity());
        fgRcv.setLayoutManager(fgLayoutManager);

        fgAdapter = new FirebaseRecyclerAdapter<ChatObj, GameItemHolder>(
                ChatObj.class, R.layout.game_list_item, GameItemHolder.class, query_game
        ) {
            @Override
            protected void populateViewHolder(final GameItemHolder viewHolder, final ChatObj model, int position) {
                final DatabaseReference curRef = getRef(position);
                final String chatId = curRef.getKey();

                ViewGroup.LayoutParams lp = viewHolder.layout.getLayoutParams();

                if(model.getType().equals(getString(R.string.chat_type_game))){
                    if(!model.isCompleted()){
                        //is friend the owner of the game

                        databaseReferenceUser.child(user.getUid()).child(getString(R.string.firebase_friend_list)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                boolean found = false;

                                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    String data = snapshot.getValue().toString();
                                    String fl_id = data.substring((data.lastIndexOf("=")+1),data.indexOf("}"));

                                    if(fl_id.equals(model.getOwner())){
                                        found = true;

                                        viewHolder.display_name.setText(model.getTitle());

                                        storageReference.child(chatId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Glide.with(getActivity())
                                                        .using(new FirebaseImageLoader())
                                                        .load(storageReference.child(chatId))
                                                        .into(viewHolder.image);
                                            }
                                        });

                                        viewHolder.button_join.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                                LayoutInflater inflater = getActivity().getLayoutInflater();
                                                View view = inflater.inflate(R.layout.chat_join_option, null);
                                                builder.setTitle(getString(R.string.game_select_option));

                                                builder.setView(view);
                                                builder.show();

                                                ImageButton ib_hunt = (ImageButton) view.findViewById(R.id.join_btn_image_hunt);
                                                ib_hunt.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        List<ChatPartiObj> partis = model.getPartis();
                                                        partis.add(new ChatPartiObj(user.getUid(), getString(R.string.chat_parti_hunter)));
                                                        databaseReferenceChat.child(chatId).child(getString(R.string.firebase_chat_participant)).setValue(partis);

                                                        Intent intent = new Intent(getActivity(), Chat_room.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_game));
                                                        bundle.putString(getString(R.string.chat_pass_id), chatId);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });

                                                ImageButton ib_spec = (ImageButton) view.findViewById(R.id.join_btn_image_spec);
                                                ib_spec.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        List<ChatPartiObj> partis = model.getPartis();
                                                        partis.add(new ChatPartiObj(user.getUid(), getString(R.string.chat_parti_analyser)));
                                                        databaseReferenceChat.child(chatId).child(getString(R.string.firebase_chat_participant)).setValue(partis);

                                                        Intent intent = new Intent(getActivity(), Chat_room.class);
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_game));
                                                        bundle.putString(getString(R.string.chat_pass_id), chatId);
                                                        intent.putExtras(bundle);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        });
                                    }
                                }

                                if(!found){
                                    ViewGroup.LayoutParams thislp = viewHolder.layout.getLayoutParams();
                                    thislp.height = 0;
                                    viewHolder.layout.setLayoutParams(thislp);
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        lp.height = 0;
                        viewHolder.layout.setLayoutParams(lp);
                    }
                } else {
                    lp.height = 0;
                    viewHolder.layout.setLayoutParams(lp);
                }
            }
        };

        fgRcv.setAdapter(fgAdapter);

        return view;
    }

    private boolean isNear(double lat, double lon){
        float[] distance = new float[2];

        getLocation();

        Location.distanceBetween( lat, lon, curLat, curLon, distance);
        Log.i(TAG, "distance: " + distance[0]);

        return distance[0] < 500;
    }

    private void getLocation(){
        gps = new GPSTracker(getActivity());

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            curLat = gps.getLatitude();
            curLon = gps.getLongitude();

        } else {
            // Can't get location.
            // GPS or network is not enabled.
            // Ask user to enable GPS/network in settings.
            gps.showSettingsAlert();
            getLocation();
            //will repeat until the user opens the GPS
        }
    }
}
