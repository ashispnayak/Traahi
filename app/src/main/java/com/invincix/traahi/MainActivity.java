package com.invincix.traahi;


import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.DialogInterface;
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
import com.onesignal.OneSignal;

import android.location.Location;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.content.SharedPreferences;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, OnRequestPermissionsResultCallback, PermissionResultCallback, BaseSliderView.OnSliderClickListener,
        ViewPagerEx.OnPageChangeListener {



    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private DatabaseReference locationDatabase, volunteerDatabase, volMainDatabase;
    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;
    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    public String latitude, longitude, ownNumber, volunteerStatus;
    public static final String STORE_DATA = "MyPrefs";
    private TextView toolbarText,texttag, logoutButton, addcontacts, safetybutton, policebutton, rtibutton, ambulancebutton, traahiVolunteer;
    public int counter;
    private SliderLayout imageSlider;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    ArrayList<String> permissions = new ArrayList<>();


    PermissionUtils permissionUtils;
    @Bind(R.id.tapBarMenu)
    TapBarMenu tapBarMenu;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);




        final Context context = this;
        permissionUtils = new PermissionUtils((Activity) context);
        ButterKnife.bind(this);

        final SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
        SharedPreferences sharedPrefContact = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        ownNumber = sharedPrefContact.getString("LOCAL_OWN_NUMBER", null);

        volunteerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("isaVolunteer");
        volMainDatabase = FirebaseDatabase.getInstance().getReference().child("Volunteers");


        imageSlider = (SliderLayout)findViewById(R.id.slider);




        HashMap<String,String> url_maps = new HashMap<String, String>();
        url_maps.put("Women Safety", "https://firebasestorage.googleapis.com/v0/b/traahiinvincix.appspot.com/o/womensafety.png?alt=media&token=cf7a2f0a-7d0d-4554-8397-2e49ff7be589");
                url_maps.put("Emergency", "https://firebasestorage.googleapis.com/v0/b/traahiinvincix.appspot.com/o/life.png?alt=media&token=89bfb621-862d-4e44-8d07-21eebbb5b55d");
                        url_maps.put("Traahi Volunteer", "https://firebasestorage.googleapis.com/v0/b/traahiinvincix.appspot.com/o/volunteer.png?alt=media&token=c0981e52-527d-401b-8342-7cc5026c7a48");


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

        mAuth = FirebaseAuth.getInstance();

        logoutButton = (TextView) findViewById(R.id.logoutButton);
        final Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/fontawesome-webfont.ttf");
        logoutButton.setTypeface(typeface);
        logoutButton.setText("\uf011");
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(context);
                }
                builder.setTitle("Logout User")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor edit = sharedPref.edit();
                                edit.clear();
                                edit.apply();
                                mAuth.signOut();

                                Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
                                startActivity(loginIntent);
                                finish();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });



        TextView horText = (TextView) findViewById(R.id.horText);
        toolbarText = (TextView)  findViewById(R.id.maintoolbartext);
        texttag = (TextView) findViewById(R.id.safeTag) ;
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        horText.setTypeface(custom);
        toolbarText.setTypeface(custom);
        texttag.setTypeface(custom);

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        //Request for permissions for api level 23 and higher
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_CONTACTS);
        permissionUtils.check_permission(permissions, "Allow Trahi to access your location and storage?", 1);


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
                if(isNetworkAvailable() && volunteerStatus != null) {
                    Log.e("Volunteer Status",volunteerStatus);
                    final LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                    final View traahiVolunteerView = layoutInflater.inflate(R.layout.traahi_volunteer, null);
                    final TextView closeButton = (TextView) traahiVolunteerView.findViewById(R.id.volCloseButton);
                    closeButton.setTypeface(typeface);
                    final Button opt = (Button) traahiVolunteerView.findViewById(R.id.optInOut);
                    if (volunteerStatus.equals("Yes")) {
                        Log.e("Inside YEs", volunteerStatus);
                        opt.setText("Opt Out From Traahi Volunteer");
                        int col = Color.parseColor("#cd0000");
                        opt.setBackgroundColor(col);
                    } else {
                        Log.e("Inside No",volunteerStatus);
                        opt.setText("Opt in for Traahi Volunteer");
                        int col1 = Color.parseColor("#339900");
                        opt.setBackgroundColor(col1);
                    }
                    final AlertDialog alertD = new AlertDialog.Builder(MainActivity.this).create();
                    alertD.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    closeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertD.dismiss();
                            ((ViewGroup) traahiVolunteerView.getParent()).removeView(traahiVolunteerView);
                        }
                    });
                    opt.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((ViewGroup) traahiVolunteerView.getParent()).removeView(traahiVolunteerView);
                            alertD.dismiss();
                            if (volunteerStatus.equals("Yes")) {
                                volunteerDatabase.setValue("No");
                                volMainDatabase.child(ownNumber).removeValue();
                                volunteerStatus = "No";
                                OneSignal.sendTag("isaVolunteer", "No");

                            } else {
                                volunteerDatabase.setValue("Yes");
                                volunteerStatus = "Yes";
                                volMainDatabase.child(ownNumber).setValue("");
                                OneSignal.sendTag("isaVolunteer", "Yes");

                            }

                        }
                    });
                    alertD.setView(traahiVolunteerView);

                    alertD.show();
                }
                else {
                    Snackbar.make(findViewById(R.id.content_main), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                            .setAction("RETRY", new View.OnClickListener() {
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

                Intent intentp = new Intent(MainActivity.this, NearestPolice.class);
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
                Intent intenth = new Intent(MainActivity.this, NearestPolice.class);
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

     displayLocation();

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        volunteerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                volunteerStatus = (String) dataSnapshot.getValue();
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





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        // redirects to utils

        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    // Callback functions


    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION","GRANTED");
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY","GRANTED");
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION","DENIED");
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION","NEVER ASK AGAIN");
    }

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
                locationDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("Location");
                locationDatabase.child("Lat").setValue(latitude);
                locationDatabase.child("Long").setValue(longitude);


            } else {
                //  displayLocationSettingsRequest(getApplicationContext());

                Log.e("Location not retrieved", "Turn on your location");
            }
        }
        else{
            displayLocationSettingsRequest(getApplicationContext());
        }
   }

    public  void  displayLocationSettingsRequest(Context context) {
        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API).build();
        googleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(10000 / 2);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "All location settings are satisfied.");
                        displayLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.i(TAG, "PendingIntent unable to execute request.");
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                        break;
                }
            }
        });

    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
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



    @OnClick(R.id.tapBarMenu) public void onMenuButtonClick() {
        Log.e("Menu","Clicked");
        tapBarMenu.toggle();
    }

    @OnClick({ R.id.share, R.id.credits, R.id.aboutus, R.id.options}) public void onMenuItemClick(View view) {
        tapBarMenu.close();
        switch (view.getId()) {
            case R.id.share:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message));
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Traahi");
                startActivity(Intent.createChooser(sharingIntent, "Share via "));
                break;
            case R.id.credits:
                Log.i("TAG", "Item 2 selected");
                Toast.makeText(getApplicationContext(), "Under Development...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.aboutus:
                Toast.makeText(getApplicationContext(), "Under Development...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.options:
                Toast.makeText(getApplicationContext(), "Under Development...", Toast.LENGTH_SHORT).show();
                break;
        }
    }


}



