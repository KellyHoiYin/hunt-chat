package com.kelly.hunt_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings_username extends AppCompatActivity implements View.OnClickListener{

    private EditText editName;
    private Button button;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private DatabaseReference checkIfExistReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_username);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editName = (EditText) findViewById(R.id.settings_username_input);
        button = (Button) findViewById(R.id.settings_username_button);

        firebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user)).child(user.getUid()).child(getString(R.string.firebase_username));
        checkIfExistReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_user));

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                editName.setText( dataSnapshot.getValue().toString() );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("SettingsUsername", databaseError.getMessage());
            }
        });

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
    public void onClick(View v) {
        if (v == button) {

            checkIfExistReference.orderByChild(getString(R.string.firebase_username)).equalTo(editName.getText().toString().trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        editName.setError(getString(R.string.error_taken_username));
                    } else {
                        databaseReference.setValue(editName.getText().toString().trim());
                        finish();
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.e("SettingsUserName", firebaseError.getMessage());
                }
            });

        }
    }
}
