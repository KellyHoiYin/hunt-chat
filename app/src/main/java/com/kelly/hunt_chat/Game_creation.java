package com.kelly.hunt_chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
import java.util.ArrayList;
import java.util.List;

public class Game_creation extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "GameCreationActivity";

    private EditText editGameTitle;
    private ImageButton btnGamePhoto;
    private ImageButton btnGameAccessInfo;
    private ImageButton btnGamePublicInfo;
    private Switch switchAccess;
    private Switch switchPublic;
    private Button btnSave;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReferenceChat;
    private DatabaseReference databaseReferenceUser;
    private StorageReference storageReference;

    private View currentView;

    private ProgressDialog progressDialog;
    private byte[] image;
    private ImageHandler imHandler;

    public static final int PICK_IMAGE = 1;
    public static final int GRANT_READ_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_creation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currentView = findViewById(android.R.id.content);
        progressDialog = new ProgressDialog(this);

        editGameTitle = (EditText) findViewById(R.id.game_creation_text_game_title);
        btnGamePhoto = (ImageButton) findViewById(R.id.game_creation_btn_image);
        btnGameAccessInfo = (ImageButton) findViewById(R.id.game_creation_info_access);
        btnGamePublicInfo = (ImageButton) findViewById(R.id.game_creation_info_public);
        switchAccess = (Switch) findViewById(R.id.game_creation_btn_access);
        switchPublic = (Switch) findViewById(R.id.game_creation_btn_public);
        btnSave = (Button) findViewById(R.id.game_creation_btn_create);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReferenceChat = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_chat));
        databaseReferenceUser = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user)).child(user.getUid());
        storageReference = FirebaseStorage.getInstance().getReference(getString(R.string.firebase_chat));

        btnGamePhoto.setOnClickListener(this);
        btnGameAccessInfo.setOnClickListener(this);
        btnGamePublicInfo.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        imHandler = new ImageHandler();
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
        if (v == btnGamePhoto) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        GRANT_READ_STORAGE_PERMISSION);
            } else {
                getImage();
            }
        }

        if (v == btnGameAccessInfo) {
            Snackbar.make(currentView, "Does this game requires special access to reach the treasure?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        if (v == btnGamePublicInfo) {
            Snackbar.make(currentView, "Does this game visible to all the players in the area?", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        if (v == btnSave) {
            createGame();
        }
    }

    private void createGame(){
        editGameTitle.setError(null);

        final String title = editGameTitle.getText().toString().trim();

        boolean cancel = false;

        if(TextUtils.isEmpty(title)){
            editGameTitle.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if(!cancel) {
            progressDialog.setMessage("Creating game...");
            progressDialog.show();

            //get the location of the user through firebase call
            databaseReferenceUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //execute every time when there is change
                    UserInformation curUser = dataSnapshot.getValue(UserInformation.class);
                    ChatObj obj = new ChatObj();
                    obj.setOwner(user.getUid());
                    obj.setType(getString(R.string.chat_type_game));
                    obj.setAuthorised(switchAccess.isChecked());
                    obj.setPubl(switchPublic.isChecked());
                    obj.setTitle(title);
                    obj.setLocation_lad(curUser.getLat());
                    obj.setLocation_long(curUser.getLon());

                    List<ChatPartiObj> partis = new ArrayList<>();
                    partis.add(new ChatPartiObj(user.getUid(), getString(R.string.chat_parti_host)));

                    obj.setPartis(partis);
                    databaseReferenceChat.push().setValue(obj, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {
                            String chatId = databaseReference.getKey();

                            if(image != null){
                                storageReference.child(chatId).putBytes(image);
                            }

                            progressDialog.dismiss();

                            finish();
                            Intent intent = new Intent(getApplicationContext(), Chat_room.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.chat_pass_type), getString(R.string.chat_type_game));
                            bundle.putString(getString(R.string.chat_pass_id), chatId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e(TAG, databaseError.getMessage());
                }
            });
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
            image = stream.toByteArray();

            btnGamePhoto.setImageBitmap(imHandler.getResizedBitmap(bmp));
            btnGamePhoto.setBackgroundColor(Color.WHITE);
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
                    Toast.makeText(getApplicationContext(), getString(R.string.permission_denied), Toast.LENGTH_SHORT).show();
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
