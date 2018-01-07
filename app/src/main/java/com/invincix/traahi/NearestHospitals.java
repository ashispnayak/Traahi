package com.invincix.traahi;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import static android.util.Log.d;


public class NearestHospitals extends  AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latitude, longitude;
    private TextView placename, placeaddress, placenumber, hospitaltoolbar;
    private Button hospitalDirection;
    private ImageButton closeButton;
    private BottomSheetBehavior mBottomSheetBehaviour1;
    public ProgressBar progBar;
    public View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_hospitals);

        view = findViewById(R.id.view_hospitalProgress);
        progBar = (ProgressBar)findViewById(R.id.hospitalProgress);
        if (progBar != null) {
            progBar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
            progBar.setIndeterminate(true);
            progBar.getIndeterminateDrawable().setColorFilter(Color.parseColor("#f73103"), android.graphics.PorterDuff.Mode.SRC_ATOP);
        }

        //Setting the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.hospitaltoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.bringToFront();

        //set toolbar text
        hospitaltoolbar = (TextView) findViewById(R.id.hospitaltoolbartext) ;
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        hospitaltoolbar.setTypeface(custom);

        //Setting the bottom Sheet

        View bottomSheet = findViewById(R.id.hospital_bottom_sheet);
        try {
            mBottomSheetBehaviour1 = BottomSheetBehavior.from(bottomSheet);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        mBottomSheetBehaviour1.setHideable(true);
        mBottomSheetBehaviour1.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.maphospital);
        mapFragment.getMapAsync(this);
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
        latitude = sharedPref.getString("LATITUDE_SAVE", null);
        longitude = sharedPref.getString("LONGITUDE_SAVE", null);
        placename = (TextView) findViewById(R.id.hospitalname);
        placeaddress = (TextView) findViewById(R.id.hospitaladdress);
        placenumber = (TextView) findViewById(R.id.hospitalnumber);
        hospitalDirection = (Button) findViewById(R.id.hospitaldirection);
        closeButton = (ImageButton) findViewById(R.id.close_button);

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.clear();
        String Hospital = "hospital";
        String url = getUrl(latitude, longitude, Hospital);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        d("onClick", url);
        checkInternet(DataTransfer);
    }
    private void checkInternet(final Object[] DataTransfer) {

        if(isNetworkAvailable()){
            progBar.setVisibility(View.VISIBLE);
            view.setVisibility(View.VISIBLE);
            new GetNearbyHospitalData().execute(DataTransfer);
        }
        else{
            Snackbar.make(findViewById(R.id.nearestHospitalLayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkInternet(DataTransfer);
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }

    }
    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private String getUrl(String latitude, String longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&key=" + "AIzaSyCMEeBmDYo4w6uh6wMyx9TgAU1nNYacIgo");
        googlePlacesUrl.append("&sensor=true");
        d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }



    class GetNearbyHospitalData extends AsyncTask<Object, String, String> implements GoogleMap.OnMarkerClickListener {

        String googlePlacesData;

        String url;

        Marker mMarker;

        @Override
        protected String doInBackground(Object... params) {
            try {
                d("GetNearbyPlacesData", "doInBackground entered");
                mMap = (GoogleMap) params[0];
                url = (String) params[1];
                DownloadUrl downloadUrl = new DownloadUrl();
                googlePlacesData = downloadUrl.readUrl(url);
                d("GooglePlacesReadTask", "doInBackground Exit");
            } catch (Exception e) {
                d("GooglePlacesReadTask", e.toString());
            }
            return googlePlacesData;
        }

        @Override
        protected void onPostExecute(String result) {
            d("GooglePlacesReadTask", "onPostExecute Entered");
            List<HashMap<String, String>> nearbyPlacesList = null;
            DataParser dataParser = new DataParser();
            nearbyPlacesList = dataParser.parse(result);
            ShowNearbyPlaces(nearbyPlacesList);
            d("GooglePlacesReadTask", "onPostExecute Exit");
        }

        private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                d("onPostExecute", "Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);

                mMarker = null;

                mMap.setOnMarkerClickListener( this);


                mMarker = mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, .05f).title(placeName + "~" + vicinity).icon(BitmapDescriptorFactory.fromResource(R.drawable.doctormarker)));
                mMarker.hideInfoWindow();

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
            progBar.setVisibility(View.GONE);
            view.setVisibility(View.GONE);
        }
        @Override
        public boolean onMarkerClick(Marker marker) {
            mMarker.hideInfoWindow();
            final Double latitude = marker.getPosition().latitude;
            final Double longitude = marker.getPosition().longitude;
            android.util.Log.d("marker lat",String.valueOf(latitude)+"63");
            String title = marker.getTitle();
            String titlepart[] = title.split("~");
            final String placeName = titlepart[0];
            String placeAddress = titlepart[1];
            String placeContact = " ";
            Log.e("clicked","marker");

            if(mBottomSheetBehaviour1.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehaviour1.setState(BottomSheetBehavior.STATE_EXPANDED);
                placename.setText(placeName);
                placeaddress.setText(placeAddress);
                if(placeContact == " "){
                    placenumber.setText("Not Available");
                }
                hospitalDirection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String uriBegin = "geo:" + latitude + "," + longitude;
                        String query = latitude + "," + longitude + "(" + placeName + ")";
                        String encodedQuery = Uri.encode(query);
                        String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
                        Uri uri = Uri.parse(uriString);
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                });
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mBottomSheetBehaviour1.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                });


            }
            else{
                mBottomSheetBehaviour1.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }


            return true;

        }

    }

}

