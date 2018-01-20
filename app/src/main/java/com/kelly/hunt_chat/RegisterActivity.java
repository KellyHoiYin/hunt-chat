package com.kelly.hunt_chat;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnRegister;
    private EditText editEmail;
    private EditText editPassword;
    private TextView textSignin;

    private EditText editDisplayName;
    private EditText editUserame;
    private Switch editPushNoti;
    private ImageButton editImage;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    private byte[] image;

    public static final int PICK_IMAGE = 1;
    public static final int GRANT_READ_STORAGE_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);;

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnRegister = (Button) findViewById(R.id.reg_btn_register);
        editEmail = (EditText) findViewById(R.id.reg_input_email);
        editPassword = (EditText) findViewById(R.id.reg_input_password);
        textSignin = (TextView) findViewById(R.id.reg_text_signIn);

        editDisplayName = (EditText) findViewById(R.id.reg_input_displayName);
        editUserame = (EditText) findViewById(R.id.reg_input_username);
        editPushNoti = (Switch) findViewById(R.id.reg_input_pushNoti);
        editImage = (ImageButton) findViewById(R.id.reg_btn_image);

        btnRegister.setOnClickListener(this);
        textSignin.setOnClickListener(this);
        editImage.setOnClickListener(this);

        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user));
        storageReference = FirebaseStorage.getInstance().getReference();
    }

    private void registerUser(){
        // Reset errors
        editEmail.setError(null);
        editPassword.setError(null);
        editUserame.setError(null);

        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        boolean cancel = false;

        if(TextUtils.isEmpty(email)){
            editEmail.setError(getString(R.string.error_field_required));
            cancel = true;
        } else {
            if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                editEmail.setError(getString(R.string.error_invalid_email));
                cancel = true;
            }
        }

        if(TextUtils.isEmpty(password)){
            editPassword.setError(getString(R.string.error_field_required));
            cancel = true;
        } else {
            if(!isPasswordValid(password)){
                editPassword.setError(getString(R.string.error_invalid_password));
                cancel = true;
            }
        }

        if(!cancel) {
            progressDialog.setMessage("Registering...");
            progressDialog.show();

            //a datasnapshot is taken everytime the user tries to register
            databaseReference.orderByChild(getString(R.string.firebase_username)).equalTo(editUserame.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        editUserame.setError(getString(R.string.error_taken_username));
                        progressDialog.dismiss();
                    } else {
                        saveUserToFirebase();
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("RegisterActivity","Firebase Data Retrieval Error during registration for the list of username");
                }
            });
        }
    }

    private void saveUserToFirebase(){
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Registered!", Toast.LENGTH_SHORT).show();

                            String name = editDisplayName.getText().toString().trim();
                            String username = editUserame.getText().toString().trim();
                            boolean push = editPushNoti.isChecked();

                            UserInformation userInformation = new UserInformation(name, username, push, true);
                            FirebaseUser user = firebaseAuth.getCurrentUser();

                            databaseReference.child(user.getUid()).setValue(userInformation);

                            storageReference.child(getString(R.string.firebase_user)).child(user.getUid()).putBytes(image);

                            finish();
                            startActivity(new Intent(getApplicationContext(), TabbedActivity.class));
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if(v == btnRegister){
            registerUser();
        }

        if(v == textSignin){
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        }

        if(v == editImage){
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        GRANT_READ_STORAGE_PERMISSION);
            } else {
                getImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA };
//
//            Cursor selectedCursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
//            selectedCursor.moveToFirst();
//
//            int columnIndex = selectedCursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = selectedCursor.getString(columnIndex);
//            selectedCursor.close();
//
//            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
//
//            ExifInterface exif = null;
//            try {
//                File pictureFile = new File(picturePath);
//                exif = new ExifInterface(pictureFile.getAbsolutePath());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            int orientation = ExifInterface.ORIENTATION_NORMAL;
//
//            if (exif != null)
//                orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    bmp = rotateBitmap(bmp, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    bmp = rotateBitmap(bmp, 180);
//                    break;
//
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    bmp = rotateBitmap(bmp, 270);
//                    break;
//            }

            Bundle extras = data.getExtras();
            //get the cropped bitmap
            Bitmap bmp = extras.getParcelable("data");

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            image = stream.toByteArray();

            editImage.setImageBitmap(getResizedBitmap(bmp));
            editImage.setBackgroundColor(Color.WHITE);
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
                    Toast.makeText(getApplicationContext(), "Permission denied, the application might not work as expected.", Toast.LENGTH_SHORT).show();
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

    public static Bitmap rotateBitmap(Bitmap bitmap, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    public Bitmap getResizedBitmap(Bitmap image) {
        int maxSize = 300;
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
