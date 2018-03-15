package com.kelly.hunt_chat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class TabbedActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public Toolbar toolbar;
    public String[] tabtitles = {"About Me", "Chats", "Nearby Games", "Friends", "Settings"};

    public static final int GRANT_READ_LOCATION_PERMISSION = 1;

    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabbed);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);     the arrow back button

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

//        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        setToolbarTitle(tabtitles[0]);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                setToolbarTitle(tabtitles[pos]);
                mViewPager.setCurrentItem(pos);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child(getString(R.string.firebase_user)).child(user.getUid());

        //check permission for the location
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    GRANT_READ_LOCATION_PERMISSION);
        } else {
            getLocation();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(TabbedActivity.this, Game_creation.class));
            }
        });

        final Intent intent = getIntent();
        if (intent.hasExtra("TabNumber")) {
            String tab = intent.getExtras().getString("TabNumber");
            switchToTab(tab);
        }

    }

    public void setToolbarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = null;

            switch(position){
                case 0:
                    fragment = new FragmentActivity_me();
                    break;
                case 1:
                    fragment = new FragmentActivity_chat();
                    break;
                case 2:
                    fragment = new FragmentActivity_game();
                    break;
                case 3:
                    fragment = new FragmentActivity_friend();
                    break;
                case 4:
                    fragment = new FragmentActivity_settings();
                    break;
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 5;
        }
    }

    public void switchToTab(String tab){
        if(tab.equals("0")){
            mViewPager.setCurrentItem(0);
        }else if(tab.equals("1")){
            mViewPager.setCurrentItem(1);
        }else if(tab.equals("2")){
            mViewPager.setCurrentItem(2);
        }else if(tab.equals("3")){
            mViewPager.setCurrentItem(3);
        }else if(tab.equals("4")){
            mViewPager.setCurrentItem(4);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case GRANT_READ_LOCATION_PERMISSION : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied, the application might not work as expected.", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void getLocation(){
        gps = new GPSTracker(this);

        // Check if GPS enabled
        if(gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();

            //update the user location when the location is determined
            Map<String, Object> updateLocation = new HashMap<>();
            updateLocation.put(getString(R.string.firebase_longitude), longitude);
            updateLocation.put(getString(R.string.firebase_latitude), latitude);

            databaseReference.updateChildren(updateLocation);
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
