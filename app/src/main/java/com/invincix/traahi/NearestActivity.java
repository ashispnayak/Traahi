package com.invincix.traahi;


import android.app.ProgressDialog;
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
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.design.widget.BottomSheetBehavior;
import android.view.View;
import android.view.animation.BounceInterpolator;
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
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

import static android.util.Log.d;


public class NearestActivity extends  AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latitude, longitude, searchType;
    private TextView placename, placeaddress, placenumber, policetoolbar, markerIcon, closeButton, locationNearest, phoneNearest;
    private Button policeDirection;

    private BottomSheetBehavior mBottomSheetBehaviour;
    private ProgressDialog loader;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest);

        //Retrieve Datas
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        searchType = extras.getString("searchType");

        loader = new ProgressDialog(this);
        loader.setMessage("Searching...");




        //Setting the Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.policetoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        assert toolbar != null;
        toolbar.bringToFront();

        //set toolbar text
        policetoolbar = (TextView) findViewById(R.id.policetoolbartext) ;
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        policetoolbar.setTypeface(custom);

        //Setting the bottom Sheet

        View bottomSheet = findViewById(R.id.police_bottom_sheet);
        try {
            mBottomSheetBehaviour = BottomSheetBehavior.from(bottomSheet);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }
        mBottomSheetBehaviour.setHideable(true);
        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mappolice);
        mapFragment.getMapAsync(this);
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
        latitude = sharedPref.getString("LATITUDE_SAVE", null);
        longitude = sharedPref.getString("LONGITUDE_SAVE", null);
        placename = (TextView) findViewById(R.id.policestationname);
        placeaddress = (TextView) findViewById(R.id.policestationaddress);
        placenumber = (TextView) findViewById(R.id.policestationnumber);
        markerIcon = (TextView) findViewById(R.id.markerIcon);
        locationNearest = (TextView) findViewById(R.id.locationNearest);
        phoneNearest = (TextView) findViewById(R.id.phoneNearest);
        policeDirection = (Button) findViewById(R.id.policedirection);
        closeButton = (TextView) findViewById(R.id.closebutton);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        markerIcon.setTypeface(typeface);
        phoneNearest.setTypeface(typeface);
        locationNearest.setTypeface(typeface);
        closeButton.setTypeface(typeface);
        markerIcon.setDrawingCacheEnabled(true);



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
        boolean success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.mapstyle));

        if (!success) {
            Log.e("", "Style parsing failed.");
        }
        mMap.clear();
        String url = getUrl(latitude, longitude, searchType);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        d("onClick", url);
        checkInternet(DataTransfer);
    }

    private void checkInternet(final Object[] DataTransfer) {

        if(isNetworkAvailable()){
            loader.show();
            new GetNearbyPoliceData().execute(DataTransfer);
        }
        else{
            Snackbar.make(findViewById(R.id.nearestPoliceLayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
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
    public boolean isNetworkAvailable() {
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




    class GetNearbyPoliceData extends AsyncTask<Object, String, String> implements GoogleMap.OnMarkerClickListener {

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

        private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
            for (int i = 0; i < nearbyPlacesList.size(); i++) {
                d("onPostExecute", "Entered into showing locations");
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("vicinity");
                LatLng latLng = new LatLng(lat, lng);

                mMarker = null;

                mMap.setOnMarkerClickListener( this);
                markerIcon.buildDrawingCache();


                if(searchType.equals("police")) {
                    markerIcon.setTextColor(Color.parseColor("#654321"));
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(placeName + "~" + vicinity).anchor(0.5f, .05f).icon(BitmapDescriptorFactory.fromBitmap(markerIcon.getDrawingCache())));
                }
                else if(searchType.equals("hospital")){
                    mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(placeName + "~" + vicinity).anchor(0.5f, .05f).icon(BitmapDescriptorFactory.fromBitmap(markerIcon.getDrawingCache())));
                }
                dropPinEffect(mMarker);
                mMarker.hideInfoWindow();

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
            loader.dismiss();
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
            LatLng latln = new LatLng(latitude,longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latln));


            Log.e("clicked","marker");

            if(mBottomSheetBehaviour.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_EXPANDED);
                placename.setText(placeName);
                placeaddress.setText(placeAddress);
                if(placeContact == " "){
                    placenumber.setText("Not Available");
                }
                policeDirection.setOnClickListener(new View.OnClickListener() {
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
                        mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                    }
                });


            }
            else{
                mBottomSheetBehaviour.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }


            return true;

        }

    }

}

