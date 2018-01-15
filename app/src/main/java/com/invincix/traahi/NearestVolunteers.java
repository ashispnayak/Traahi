package com.invincix.traahi;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.util.HashMap;
import java.util.List;

import static android.util.Log.d;

public class NearestVolunteers extends AppCompatActivity implements OnMapReadyCallback {
    private  TextView toolbarText;
    private GoogleMap mMap;
    private Marker mMarker;
    private  Transformation transformation;
    private ProgressDialog loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_volunteers);

        loader = new ProgressDialog(this);


        transformation = new RoundedTransformationBuilder()
                .borderColor(Color.WHITE)
                .borderWidthDp(1)
                .cornerRadiusDp(100)
                .oval(false)
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.nearestVoltoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.bringToFront();
        toolbarText = (TextView)  findViewById(R.id.nearestVolToolbartext);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        toolbarText.setTypeface(custom);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nearestVolunteersMap);
        mapFragment.getMapAsync(this);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.mapstyle));

        if (!success) {
            Log.e("", "Style parsing failed.");
        }
        mMap.clear();

        plotPoints();
        android.os.Handler UI_HANDLER = new android.os.Handler();
        UI_HANDLER.postDelayed(UI_UPDATE_RUNNABLE, 3000);


    }

    private void plotPoints() {
        mMarker = null;
        loader.setMessage("Searching...");
        loader.show();

        DatabaseReference volunteerDatabase = FirebaseDatabase.getInstance().getReference().child("Volunteers");
        volunteerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key =  ds.getKey();

                    DatabaseReference keyReference = FirebaseDatabase.getInstance().getReference().child("Volunteers").child(key);
                    keyReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap value = (HashMap) dataSnapshot.getValue();
                            if (value != null) {
                                Double Lat = Double.parseDouble((String) dataSnapshot.child("Profile").child("Location").child("Lat").getValue());
                                Double Long = Double.parseDouble((String) dataSnapshot.child("Profile").child("Location").child("Long").getValue());
                                String picUrl = (String) dataSnapshot.child("Profile").child("Profile").child("picUrl").getValue();
                                String userName = (String) dataSnapshot.child("Profile").child("Profile").child("Name").getValue();

                                LatLng latlng = new LatLng(Lat, Long);

                                if (picUrl != null) {
                                    mMarker = mMap.addMarker(new MarkerOptions().position(latlng).title(userName).anchor(0.5f, .05f));
                                    PicassoMarker marker = new PicassoMarker(mMarker);
                                    Picasso.with(NearestVolunteers.this).load(picUrl).resize(150, 150).transform(transformation).into(marker);
                                } else  {

                                    mMarker = mMap.addMarker(new MarkerOptions().position(latlng).title(userName).anchor(0.5f, .05f).icon(BitmapDescriptorFactory.fromResource(R.drawable.defaultprofpic)));
                                }

                                dropPinEffect(mMarker);

                                //move map camera
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latlng));
                                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("", "Read failed");
                        }
                    });

                }

                loader.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });


    }

    Runnable UI_UPDATE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
           plotPoints();
        }
    };

    private void dropPinEffect(final Marker marker) {
        // Handler allows us to repeat a code block after a specified delay
        final android.os.Handler handler = new android.os.Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1500;

        // Use the bounce interpolator
        final android.view.animation.Interpolator interpolator =
                new BounceInterpolator();

        // Animate marker with a bounce updating its position every 15ms
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                // Calculate t for bounce based on elapsed time
                float t = Math.max(
                        1 - interpolator.getInterpolation((float) elapsed
                                / duration), 0);
                // Set the anchor
                marker.setAnchor(0.5f, 1.0f + 14 * t);

                if (t > 0.0) {
                    // Post this event again 15ms from now.
                    handler.postDelayed(this, 15);
                } else { // done elapsing, show window

                }
            }
        });
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



}
