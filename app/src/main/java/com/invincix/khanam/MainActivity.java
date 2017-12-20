package com.invincix.khanam;


import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.google.firebase.auth.FirebaseAuth;
import com.huxq17.swipecardsview.SwipeCardsView;
import com.michaldrabik.tapbarmenulib.TapBarMenu;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


import java.util.ArrayList;
import java.util.List;

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

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements ConnectionCallbacks, OnConnectionFailedListener, OnRequestPermissionsResultCallback, PermissionResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    private Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 10; // 10 meters

    private SwipeCardsView swipeCardsView;
    private List<MainPageCardModel> modelList = new ArrayList<>();
    private ImageButton addcontacts, safetybutton, policebutton, muncipalitybutton, rtibutton, ambulancebutton;
    public String latitude;
    public String longitude;
    public static final String STORE_DATA = "MyPrefs";
    private TextView toolbarText;
    public int counter;
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
        Context context = this;
        permissionUtils = new PermissionUtils((Activity) context);
        ButterKnife.bind(this);

        swipeCardsView = (SwipeCardsView)findViewById(R.id.SwipecardView);
        swipeCardsView.retainLastCard(true);
        swipeCardsView.enableSwipe(true);

        swipeCardsView.setCardsSlideListener(new SwipeCardsView.CardsSlideListener() {
            @Override
            public void onShow(int index) {
                Log.e("Index",String.valueOf(index));
            }

            @Override
            public void onCardVanish(int index, SwipeCardsView.SlideType type) {
                switch (type){
                    case LEFT:

                        break;
                    case RIGHT:

                        break;
                }
            }

            @Override
            public void onItemClick(View cardImageView, int index) {
                Toast.makeText(getApplicationContext(), "Index" + String.valueOf(index), Toast.LENGTH_SHORT).show();
            }
        });

        getCardData();



     /*   mAuth=FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null){
                    Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }

            }
        };*/
        TextView horText = (TextView) findViewById(R.id.horText);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        horText.setTypeface(custom);

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
        addcontacts = (ImageButton) findViewById(R.id.addcontacts);
        addcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddContacts.class);
                startActivity(intent);
            }
        });

        //nearest policestation
        policebutton = (ImageButton) findViewById(R.id.policebutton);
        policebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentp = new Intent(MainActivity.this, NearestPolice.class);
                startActivity(intentp);


            }
        });

        //nearest ambulance
        ambulancebutton = (ImageButton) findViewById(R.id.ambubutton);
        ambulancebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenth = new Intent(MainActivity.this, NearestHospitals.class);
                startActivity(intenth);

            }
        });

        //nearest pcr
        muncipalitybutton = (ImageButton) findViewById(R.id.municipalitybutton);
        muncipalitybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Under Development...", Toast.LENGTH_SHORT).show();


            }
        });

        //safety
        safetybutton = (ImageButton) findViewById(R.id.safetybutton);
        safetybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SafetyTips.class);
                startActivity(intent);

            }
        });

        //rti process
        rtibutton = (ImageButton) findViewById(R.id.rtibutton);
        rtibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentrti = new Intent(MainActivity.this, RtiProcess.class);
                startActivity(intentrti);

            }
        });


        //Retrieve Datas

        SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
        counter = (sharedPref.getInt("CONTACT_NUMBER", -1));
        for (int i = 0; i <= counter; i++) {
            sharedPref.getString("LOCAL_PHONE_NUMBER_" + String.valueOf(i), null);
            sharedPref.getString("LOCAL_CONTACT_NAME_" + String.valueOf(i), null);

        }

        String data_name = (sharedPref.getString("LOCAL_NAME", null));


        longitude = " ";
        latitude = " ";

        Log.e("longitude", longitude);


        //check if name is null, if its null set its value
        if (data_name == null) {
            permissionUtils.check_permission(permissions, "Allow Trahi to write data?", 2);

            AlertDialog.Builder namegetter = new AlertDialog.Builder(MainActivity.this, R.style.MyAlertDialogStyle);

            namegetter.setTitle("Enter Your Name");
            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            namegetter.setView(input);

            namegetter.setIcon(R.drawable.ic_love);
            namegetter.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            String name = input.getText().toString();
                            Log.e(name, " name");
                            editor.putString("LOCAL_NAME", name);
                            editor.apply();
                            Intent intent = getIntent();
                            finish();
                            startActivity(intent);
                        }
                    });

            namegetter.show();
        }
     displayLocation();


    }

    private void getCardData() {
        modelList.add(new MainPageCardModel("https://firebasestorage.googleapis.com/v0/b/traahi-invincians.appspot.com/o/womensafety.png?alt=media&token=357cd312-e06c-4112-8d22-4f604f1d2b36"));
        modelList.add(new MainPageCardModel("https://i.pinimg.com/originals/89/8d/70/898d70a79d51a944cd247b5fd0a1ab5a.jpg"));
        modelList.add(new MainPageCardModel("http://www.topdesignmag.com/wp-content/uploads/2011/05/347.jpg"));

        MainPageCardAdapter cardAdapter = new MainPageCardAdapter(modelList,this);
        swipeCardsView.setAdapter(cardAdapter);
    }

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
           Log.e("Latitude",String.valueOf(latitude));
           SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
           SharedPreferences.Editor editor = sharedPref.edit();
           editor.putString("LATITUDE_SAVE", latitude);
           editor.putString("LONGITUDE_SAVE", longitude);
           editor.apply();


       } else {

           Log.e("Location not retrieved","Turn on your location");
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



