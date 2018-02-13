package com.kelly.hunt_chat;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
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

import java.io.ByteArrayOutputStream;

/**
 * Created by User on 11/14/2017.
 */

public class FragmentActivity_settings extends Fragment implements View.OnClickListener{

    final private String TAG = "SettingsFragment";

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private ImageView textImage;
    private TextView textDisplayName;
    private TextView textUsername;
    private TextView textEmail;
    private Switch switchAddByID;
    private Switch switchIsPush;

    private Button btnLogout;

    private RelativeLayout layoutProfilePic;
    private RelativeLayout layoutDisplayName;
    private RelativeLayout layoutUsername;
    private RelativeLayout layoutEmail;
    private RelativeLayout layoutPassword;

    private ImageHandler imHandler;

    public static final int PICK_IMAGE = 1;
    public static final int GRANT_READ_STORAGE_PERMISSION = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.tabbed_settings, container, false);

//        ((TabbedActivity)getActivity()).toolbar.setTitle("Settings");

        textImage = (ImageView) view.findViewById(R.id.settings_tabbed_profile_pic_value);
        textDisplayName = (TextView) view.findViewById(R.id.settings_tabbed_display_name_value);
        textUsername = (TextView) view.findViewById(R.id.settings_tabbed_username_value);
        textEmail = (TextView) view.findViewById(R.id.settings_tabbed_email_value);
        switchAddByID = (Switch) view.findViewById(R.id.settings_tabbed_addID_value);
        switchIsPush = (Switch) view.findViewById(R.id.settings_tabbed_push_value);

        btnLogout = (Button) view.findViewById(R.id.settings_tabbed_logout);

        layoutProfilePic = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_profile_pic);
        layoutDisplayName = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_display_name);
        layoutUsername = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_username);
        layoutEmail = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_email);
        layoutPassword = (RelativeLayout) view.findViewById(R.id.settings_tabbed_layout_password);

        imHandler = new ImageHandler();

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
                switchAddByID.setChecked( curUser.isAddUsername() );
                textEmail.setText( user.getEmail() );
                switchIsPush.setChecked( curUser.isPush() );
                switchAddByID.setChecked( curUser.isAddUsername() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, databaseError.getMessage());
            }
        });

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getActivity()/* context */)
                        .using(new FirebaseImageLoader())
                        .load(storageReference)
                        .into(textImage);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                textImage.setImageDrawable(getResources().getDrawable(R.drawable.ic_texture_black_24dp));
            }
        });

        switchAddByID.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                databaseReference.child(getString(R.string.firebase_add_by_username)).setValue(isChecked);
            }
        });

        switchIsPush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                databaseReference.child(getString(R.string.firebase_push)).setValue(isChecked);
            }
        });

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
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        GRANT_READ_STORAGE_PERMISSION);
            } else {
                getImage();
            }
        }

        if(v == layoutDisplayName){
            startActivity(new Intent(getActivity(), Settings_display_name.class));
        }

        if(v == layoutUsername){
            startActivity(new Intent(getActivity(), Settings_username.class));
        }

        if(v == layoutEmail){
            startActivity(new Intent(getActivity(), Settings_email.class));
        }

        if(v == layoutPassword){
            startActivity(new Intent(getActivity(), Settings_password.class));
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && null != data) {

            Bundle extras = data.getExtras();
            //get the cropped bitmap
            Bitmap bmp = extras.getParcelable("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//            image = stream.toByteArray();

            textImage.setImageBitmap(imHandler.getResizedBitmap(bmp));
            textImage.setBackgroundColor(Color.WHITE);

            storageReference.putBytes(stream.toByteArray());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GRANT_READ_STORAGE_PERMISSION : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getImage();
                } else {
                    Toast.makeText(getActivity(), "Permission denied, the application might not work as expected.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void getImage(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("crop", true);
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_IMAGE);
    }
}
