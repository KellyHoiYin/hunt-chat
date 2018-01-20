package com.kelly.hunt_chat;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by User on 11/14/2017.
 */

public class FragmentActivity_settings extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ImageView textImage;
    private TextView textDisplayName;
    private TextView textUsername;
    private TextView textEmail;
    private Switch textAddByID;
    private Switch textIsPush;

    private Button btnLogout;

    private RelativeLayout layoutProfilePic;
    private RelativeLayout layoutDisplayName;
    private RelativeLayout layoutUsername;
    private RelativeLayout layoutEmail;
    private RelativeLayout layoutPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.tabbed_settings, container, false);

//        ((TabbedActivity)getActivity()).toolbar.setTitle("Settings");

        textImage = (ImageView) view.findViewById(R.id.settings_tabbed_profile_pic_value);
        textDisplayName = (TextView) view.findViewById(R.id.settings_tabbed_display_name_value);
        textUsername = (TextView) view.findViewById(R.id.settings_tabbed_username_value);
        textEmail = (TextView) view.findViewById(R.id.settings_tabbed_email_value);
        textAddByID = (Switch) view.findViewById(R.id.settings_tabbed_addID_value);
        textIsPush = (Switch) view.findViewById(R.id.settings_tabbed_push_value);

        btnLogout = (Button) view.findViewById(R.id.settings_tabbed_logout);

        layoutProfilePic = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_profile_pic);
        layoutDisplayName = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_display_name);
        layoutUsername = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_username);
        layoutEmail = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_email);
        layoutPassword = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_password);

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user)).child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_user)).child(user.getUid());

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //execute every time when there is change
                UserInformation curUser = dataSnapshot.getValue(UserInformation.class);
                textDisplayName.setText( curUser.getName() );
                textUsername.setText( curUser.getUsername() );
                textAddByID.setChecked( curUser.isAddUsername() );
                textEmail.setText( user.getEmail() );
                textIsPush.setChecked( curUser.isPush() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FragementSettings", databaseError.getMessage());
            }
        });

        Glide.with(this /* context */)
                .using(new FirebaseImageLoader())
                .load(storageReference)
                .into(textImage);

        btnLogout.setOnClickListener(this);
        layoutProfilePic.setOnClickListener(this);
        layoutDisplayName.setOnClickListener(this);
        layoutUsername.setOnClickListener(this);
        layoutEmail.setOnClickListener(this);
        layoutPassword.setOnClickListener(this);

        return view;
    }


    @Override
    public void onClick(View v) {
        if(v == btnLogout){
            firebaseAuth.signOut();
            getActivity().finish();
            startActivity(new Intent(getActivity(), LoginActivity.class));
        }

        if(v == layoutProfilePic){
            Toast.makeText(this.getActivity(), "Layout Profile clicked", Toast.LENGTH_SHORT).show();
        }

        if(v == layoutDisplayName){
            Toast.makeText(this.getActivity(), "Layout Display name clicked", Toast.LENGTH_SHORT).show();
        }

        if(v == layoutUsername){
            Toast.makeText(this.getActivity(), "Layout Username clicked", Toast.LENGTH_SHORT).show();
        }

        if(v == layoutEmail){
            Toast.makeText(this.getActivity(), "Layout Email clicked", Toast.LENGTH_SHORT).show();
        }

        if(v == layoutPassword){
            Toast.makeText(this.getActivity(), "Layout Password clicked", Toast.LENGTH_SHORT).show();
        }
    }
}
