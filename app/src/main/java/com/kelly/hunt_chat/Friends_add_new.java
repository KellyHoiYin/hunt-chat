package com.kelly.hunt_chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Friends_add_new extends AppCompatActivity implements View.OnClickListener{

    private EditText inputUsername;
    private RelativeLayout layout;
    private ImageView userImage;
    private TextView userDisplayName;
    private Button addButton;
    private TextView notFoundText;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_add_new);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        inputUsername = (EditText) findViewById(R.id.friends_add_input);
        layout = (RelativeLayout) findViewById(R.id.friends_add_layout);
        userImage = (ImageView) findViewById(R.id.friends_add_profile_pic);
        userDisplayName = (TextView) findViewById(R.id.friends_add_name);
        addButton = (Button) findViewById(R.id.friends_add_button);
        notFoundText = (TextView) findViewById(R.id.friends_add_username_not_found);

        inputUsername.requestFocus();
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        layout.setVisibility(View.GONE);
        userImage.setVisibility(View.GONE);
        userDisplayName.setVisibility(View.GONE);
        addButton.setVisibility(View.GONE);
        notFoundText.setVisibility(View.GONE);

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user));
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_user));

        inputUsername.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    layout.setVisibility(View.VISIBLE);

                    userImage.setVisibility(View.GONE);
                    userDisplayName.setVisibility(View.GONE);
                    addButton.setVisibility(View.GONE);
                    notFoundText.setVisibility(View.GONE);

                    databaseReference.orderByChild(getString(R.string.firebase_username)).equalTo(inputUsername.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                String snapshot = dataSnapshot.getValue().toString();

                                final String id = snapshot.split("=")[0].substring(1);
                                userID = id;
                                String displayField = snapshot.split("=")[3];
                                userDisplayName.setText(displayField.substring(0, displayField.indexOf(",") ));

                                storageReference.child(id).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Glide.with(getApplicationContext())
                                                .using(new FirebaseImageLoader())
                                                .load(storageReference.child(id))
                                                .into(userImage);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        userImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_texture_black_24dp));
                                    }
                                });

                                userImage.setVisibility(View.VISIBLE);
                                userDisplayName.setVisibility(View.VISIBLE);
                                addButton.setVisibility(View.VISIBLE);
                            } else {
                                notFoundText.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.e("AddFriendsByName", firebaseError.getMessage());
                        }
                    });

                    return true;
                }

                return false;
            }
        });

        addButton.setOnClickListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == addButton){
            FirebaseUser user = firebaseAuth.getCurrentUser();

            DatabaseReference newReq = databaseReference.child(userID).child(getString(R.string.firebase_friend_requests));

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
            Calendar cal = Calendar.getInstance();

            FriendRequestObj fr = new FriendRequestObj(user.getUid(), false, dateFormat.format(cal.getTime()));
            newReq.push().setValue(fr);

            Toast.makeText(this, getString(R.string.send_friend_req), Toast.LENGTH_SHORT).show();
        }
    }

}
