package com.kelly.hunt_chat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Settings_password extends AppCompatActivity implements View.OnClickListener{

    private EditText editPassword;
    private EditText editPasswordConfirm;
    private Button button;

    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_password);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editPassword = (EditText) findViewById(R.id.settings_password_input);
        editPasswordConfirm = (EditText) findViewById(R.id.settings_password_input_confirm);
        button = (Button) findViewById(R.id.settings_password_button);

        firebaseAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(this);
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
    public void onClick(View v){
        if(v == button){
            editPassword.setError(null);
            editPasswordConfirm.setError(null);

            final String pass1 = editPassword.getText().toString().trim();
            final String pass2 = editPasswordConfirm.getText().toString().trim();

            if(TextUtils.equals(pass1, pass2)) {

                if(isPasswordValid(pass1)) {

                    final FirebaseUser user = firebaseAuth.getCurrentUser();

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Enter your current password to proceed");

                    final EditText input = new EditText(this);
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    builder.setView(input);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String d_password = input.getText().toString();

                            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), d_password);
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updatePassword(pass1)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    finish();
                                                                } else {
                                                                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                } else {
                    editPassword.setError(getString(R.string.error_password_constraint));
                    Toast.makeText(this, getString(R.string.error_invalid_password), Toast.LENGTH_SHORT).show();
                }
            } else {
                editPasswordConfirm.setError(getString(R.string.error_password_not_match));
                Toast.makeText(this, getString(R.string.error_password_not_match), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isPasswordValid(String password) {
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }
}
