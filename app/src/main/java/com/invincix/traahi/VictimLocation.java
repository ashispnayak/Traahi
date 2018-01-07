package com.invincix.traahi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class VictimLocation extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Marker mMarker;
    private String lat, lng;
    private LatLng location;
    private Button victimDirection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_victim_location);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        lat = extras.getString("lat");
        lng = extras.getString("lng");
        final double latitude = Double.parseDouble(lat);
        final double longitude = Double.parseDouble(lng);
        location = new LatLng(latitude,longitude);
        victimDirection = (Button) findViewById(R.id.victimDirections);
        victimDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uriBegin = "geo:" + latitude + "," + longitude;
                String query = latitude + "," + longitude  ;
                String encodedQuery = Uri.encode(query);
                String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                Uri uri = Uri.parse(uriString);
                Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        //Setting the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.victimtoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.bringToFront();

        //set toolbar text
        TextView victimtoolbar = (TextView) findViewById(R.id.victimtoolbartext) ;
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        victimtoolbar.setTypeface(custom);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapvictim);
        mapFragment.getMapAsync(this);




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.clear();
        mMarker = null;

        mMarker = mMap.addMarker(new MarkerOptions().position(location).title("Victim's Location").anchor(0.5f, .05f));
        mMarker.hideInfoWindow();

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

    }
}
