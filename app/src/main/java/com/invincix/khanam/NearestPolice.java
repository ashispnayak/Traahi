package com.invincix.khanam;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.support.design.widget.BottomSheetBehavior;
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

import java.util.HashMap;
import java.util.List;

import static android.util.Log.d;


public class NearestPolice extends  AppCompatActivity  implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String latitude, longitude;
    private TextView placename, placeaddress, placenumber, policetoolbar;
    private Button policeDirection;
    private ImageButton closeButton;
    private BottomSheetBehavior mBottomSheetBehaviour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_police);

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
        policeDirection = (Button) findViewById(R.id.policedirection);
        closeButton = (ImageButton) findViewById(R.id.closebutton);

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
        String Police = "police";
        String url = getUrl(latitude, longitude, Police);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        d("onClick", url);
        new GetNearbyPoliceData().execute(DataTransfer);
    }


    private String getUrl(String latitude, String longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + 10000);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&key=" + "AIzaSyAr9qKZuxiXtvrqSzIcYNjEAI2wFvYmCuo");
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


                mMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(placeName + "~" + vicinity).icon(BitmapDescriptorFactory.fromResource(R.drawable.policemarker)));
                mMarker.hideInfoWindow();

                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
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

