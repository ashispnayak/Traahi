package com.invincix.traahi;


import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Build;
import android.os.Bundle;

import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;





import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import com.squareup.picasso.Picasso;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import android.location.Location;

import android.support.v4.app.ActivityCompat;

import android.content.SharedPreferences;

import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, BaseSliderView.OnSliderClickListener, OnMenuItemClickListener,
        ViewPagerEx.OnPageChangeListener {



    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private DatabaseReference locationDatabase, volunteerDatabase, volMainDatabase, newsDatabase;
    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;




    public String latitude, longitude, ownNumber, volunteerStatus;
    public static final String STORE_DATA = "MyPrefs";
    private TextView toolbarText,texttag, newsText, addcontacts, safetybutton, policebutton, rtibutton, ambulancebutton, traahiVolunteer;
    private SliderLayout imageSlider;
    private FirebaseAuth mAuth;
    private ContextMenuDialogFragment  mMenuDialogFragment;
    private FragmentManager fragmentManager;
    final Context context = this;
    private HashMap location;
    SharedPreferences sharedPref;
    HashMap<String,String> url_maps;
    private Button addNews;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        newsDatabase = FirebaseDatabase.getInstance().getReference().child("News");


        url_maps = new HashMap<>();
        imageSlider = (SliderLayout)findViewById(R.id.slider);


        addNews();

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        fragmentManager = getSupportFragmentManager();
        initMenuFragment();
        mMenuDialogFragment.setItemClickListener(this);


        Toolbar toolbar = (Toolbar) findViewById(R.id.maintoolbar);
        toolbar.showOverflowMenu();

        setSupportActionBar(toolbar);

        sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
        SharedPreferences sharedPrefContact = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        ownNumber = sharedPrefContact.getString("LOCAL_OWN_NUMBER", null);

        volunteerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("isaVolunteer");
        volMainDatabase = FirebaseDatabase.getInstance().getReference().child("Volunteers");










        mAuth = FirebaseAuth.getInstance();

        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");




        locationDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("Location");




        TextView horText = (TextView) findViewById(R.id.horText);
        toolbarText = (TextView)  findViewById(R.id.maintoolbartext);
        texttag = (TextView) findViewById(R.id.safeTag) ;
        newsText = (TextView) findViewById(R.id.newsText);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        horText.setTypeface(custom);
        toolbarText.setTypeface(custom);
        texttag.setTypeface(custom);
        newsText.setTypeface(custom);


        displayLocation();

        isNetworkAvailable();





        //add News
        addNews = (Button) findViewById(R.id.addNews);
        addNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialogForNews();
            }
        });

        //add contacts
        addcontacts = (TextView) findViewById(R.id.addcontacts);
        addcontacts.setTypeface(typeface);
        addcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddContacts.class);
                startActivity(intent);
            }
        });



        //be a Traahi Volunteer
        traahiVolunteer = (TextView) findViewById(R.id.traahiVolunteer) ;
        traahiVolunteer.setTypeface(typeface);

        traahiVolunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable() && volunteerStatus != null && location != null) {
                    Intent intent = new Intent(MainActivity.this,TraahiVolunteer.class);
                    Bundle extras = new Bundle();
                    Log.e("volunteerStatusIn",volunteerStatus);
                    extras.putString("volunteerStatus",volunteerStatus);
                    intent.putExtras(extras);
                    startActivity(intent);

                }
                else{
                    Snackbar.make(findViewById(R.id.content_main), "No Internet Connection", Snackbar.LENGTH_SHORT)
                            .setAction("Ok", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                }
                            })
                            .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                            .show();
                }


            }
        });

        //nearest policestation
        policebutton = (TextView) findViewById(R.id.policebutton);
        policebutton.setTypeface(typeface);
        policebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intentp = new Intent(MainActivity.this, NearestActivity.class);
                Bundle extras = new Bundle();
                extras.putString("searchType","police");
                intentp.putExtras(extras);
                startActivity(intentp);


            }
        });

        //nearest ambulance
        ambulancebutton = (TextView) findViewById(R.id.ambubutton);
        ambulancebutton.setTypeface(typeface);
        ambulancebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenth = new Intent(MainActivity.this, NearestActivity.class);
                Bundle extras = new Bundle();
                extras.putString("searchType","hospital");
                intenth.putExtras(extras);
                startActivity(intenth);

            }
        });



        //safety
        safetybutton = (TextView) findViewById(R.id.safetybutton);
        safetybutton.setTypeface(typeface);
        safetybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SafetyTips.class);
                startActivity(intent);

            }
        });

        //rti process
        rtibutton = (TextView) findViewById(R.id.rtibutton);
        rtibutton.setTypeface(typeface);
        rtibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Toast.makeText(getApplicationContext(),"Under Development....",Toast.LENGTH_SHORT).show();

            }
        });


        //Retrieve Datas
        longitude = " ";
        latitude = " ";



    }

    private void createDialogForNews() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Traahi News");
        builder.setMessage("Traahi News is a way to showcase any of your heroic deeds. A post after creation will go through admin check. The best posts will be featured in the app. ");
        builder.setPositiveButton("Create a post", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(MainActivity.this, CreateNews.class);
                Bundle extras = new Bundle();
                extras.putString("Number",ownNumber);
                intent.putExtras(extras);
                startActivity(intent);

                dialog.cancel();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void addNews() {
        newsDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()) {
                    String key =  ds.getKey();

                    DatabaseReference keyReference = FirebaseDatabase.getInstance().getReference().child("News").child(key);
                    keyReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            HashMap value = (HashMap) dataSnapshot.getValue();
                            if (value != null) {
                                String title = (String) dataSnapshot.child("Title").getValue();
                                String picUrl = (String) dataSnapshot.child("picUrl").getValue();
                                url_maps.put(title,picUrl);
                                loadNews();
                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d("", "Read failed");
                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }

    private void loadNews() {
        imageSlider.removeAllSliders();
        for(String name : url_maps.keySet()){
            TextSliderView textSliderView = new TextSliderView(this);
            // initialize a SliderLayout
            textSliderView
                    .description(name)
                    .image(url_maps.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);

            //add your extra information
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra",name);

            imageSlider.addSlider(textSliderView);
        }
        imageSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        imageSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        imageSlider.setCustomAnimation(new DescriptionAnimation());
        imageSlider.setDuration(4000);
        imageSlider.addOnPageChangeListener(this);
    }


    private void initMenuFragment(){
        Log.e("Inside","Init Menu");
        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.tool_bar_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(false);
        // set other settings to meet your needs
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
    }
    private List<MenuObject> getMenuObjects (){

        List<MenuObject> menuObjects = new ArrayList<>();
        MenuObject close = new MenuObject();
        close.setResource(R.drawable.cancel);
        close.setBgColor(Color.parseColor("#f73103"));
        MenuObject profile = new MenuObject("Profile");
        profile.setResource(R.drawable.users);
        profile.setBgColor(Color.parseColor("#f73103"));
        MenuObject share = new MenuObject("Share");
        share.setResource(R.drawable.ic_share);
        share.setBgColor(Color.parseColor("#f73103"));
        MenuObject credits = new MenuObject("Credits");
        credits.setResource(R.drawable.ic_credits);
        credits.setBgColor(Color.parseColor("#f73103"));


        menuObjects.add(close);
        menuObjects.add(profile);
        menuObjects.add(share);
        menuObjects.add(credits);


        return menuObjects;

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        volunteerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                volunteerStatus = (String) dataSnapshot.getValue();
                if(volunteerStatus == "Yes" && latitude !=null && longitude != null){
                    volMainDatabase.child("Profile").child("Location").child("Lat").setValue(latitude);
                    volMainDatabase.child("Profile").child("Location").child("Long").setValue(longitude);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        locationDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                location = (HashMap) dataSnapshot.getValue();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onStop() {
        imageSlider.startAutoCycle();
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        Log.e("Menu Created","");

        return (super.onCreateOptionsMenu(menu));

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.context_menu:
                if(fragmentManager.findFragmentByTag(ContextMenuDialogFragment.TAG) == null) {
                    mMenuDialogFragment.show(fragmentManager, "ContextMenuDialogFragment");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(this,slider.getBundle().get("extra") + "",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.e("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}


    private void displayLocation() {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
       boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gps_enabled) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mLastLocation = LocationServices.FusedLocationApi
                    .getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = String.valueOf(mLastLocation.getLatitude());
                longitude = String.valueOf(mLastLocation.getLongitude());
                Log.e("Latitude", String.valueOf(latitude));
                SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("LATITUDE_SAVE", latitude);
                editor.putString("LONGITUDE_SAVE", longitude);
                editor.apply();
                locationDatabase.child("Lat").setValue(latitude);
                locationDatabase.child("Long").setValue(longitude);




            } else {
                //  displayLocationSettingsRequest(getApplicationContext());

                Log.e("Location not retrieved", "Turn on your location");
            }
        }
        else{
            displayLocationSettingsRequest(mGoogleApiClient);
        }

   }


    public  void  displayLocationSettingsRequest(GoogleApiClient mGoogleApiClient) {

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        displayLocation();
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
// Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        displayLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        displayLocationSettingsRequest(mGoogleApiClient);//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(),
                        "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }


    @Override
    public void onMenuItemClick(View clickedView, int position) {
        if(position == 2){
            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                          sharingIntent.setType("text/plain");
                           sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                           sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Traahi");
             startActivity(Intent.createChooser(sharingIntent, "Share via "));
        }
        else if( position == 3){
            Toast.makeText(getApplicationContext(),"Under Development...",Toast.LENGTH_SHORT).show();
        }
        else if(position  == 1){
            if(isNetworkAvailable()) {
                Intent intent = new Intent(MainActivity.this,ProfileActivity.class);
                startActivity(intent);

            }
            else{
                Snackbar.make(findViewById(R.id.content_main), "No Internet Connection", Snackbar.LENGTH_SHORT)
                        .setAction("Ok", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        })
                        .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                        .show();
            }
        }

    }
}



