package com.kelly.hunt_chat;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    // UI references
    private EditText editEmail;
    private EditText editPassword;
    private Button btnSignIn;
    private TextView textRegister;

    private ProgressDialog pd;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        //direct to the TabbedActivity when there is active user signed in
        if(firebaseAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(getApplicationContext(), TabbedActivity.class));
        }

        editEmail = (EditText) findViewById(R.id.login_input_Email);
        editPassword = (EditText) findViewById(R.id.login_input_Password);

        btnSignIn = (Button) findViewById(R.id.login_btn_signIn);
        textRegister = (TextView) findViewById(R.id.login_text_register);

        btnSignIn.setOnClickListener(this);
        textRegister.setOnClickListener(this);

        pd = new ProgressDialog(this);
    }

    private void attemptLogin() {
        // Reset errors
        editEmail.setError(null);
        editPassword.setError(null);

        // Store values at the time of the login attempt
        String email = editEmail.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        boolean cancel = false;

        // Check for a null password field
        if (TextUtils.isEmpty(password)) {
            editPassword.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        // Check for a null email address field
        if (TextUtils.isEmpty(email)) {
            editEmail.setError(getString(R.string.error_field_required));
            cancel = true;
        }

        if(!cancel) {
            pd.setMessage("Logging in...");
            pd.show();

            firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pd.dismiss();

                            if (task.isSuccessful()) {
                                checkIfEmailVerified();
                            } else {
                                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }

    }

    @Override
    public void onClick(View v) {
        if(v == btnSignIn){
            attemptLogin();
        }

        if(v == textRegister){
            finish();
            startActivity(new Intent(this, RegisterActivity.class));
        }
    }

    private void checkIfEmailVerified(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user.isEmailVerified()){
            finish();
            startActivity(new Intent(getApplicationContext(), TabbedActivity.class));
        } else {
            Toast.makeText(this, "Email is not yet verified. A new verification email will be sent.", Toast.LENGTH_SHORT).show();
            sendVerificationEmail();
        }
    }

    private void sendVerificationEmail(){
        FirebaseUser user = firebaseAuth.getCurrentUser();

        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            firebaseAuth.signOut();
                            Toast.makeText(getApplicationContext(), "A new verification email is sent. Please verify to login.", Toast.LENGTH_SHORT).show();
                        } else {
                            firebaseAuth.signOut();
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}

