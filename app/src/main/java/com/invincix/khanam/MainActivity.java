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

import com.michaldrabik.tapbarmenulib.TapBarMenu;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
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
        implements  OnRequestPermissionsResultCallback, PermissionResultCallback {


    private ImageButton addcontacts,safetybutton,policebutton,pcrbutton,rtibutton,ambulancebutton;
    public String latitude;
    public String longitude;
    public static final String STORE_DATA = "MyPrefs";
    private TextView toolbarText;
    public int counter;
    ArrayList<String> permissions = new ArrayList<>();




    PermissionUtils permissionUtils;
    @Bind(R.id.tapBarMenu) TapBarMenu tapBarMenu;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);
        Context context =this;
        permissionUtils = new PermissionUtils((Activity) context);
        ButterKnife.bind(this);



        //Request for permissions for api level 23 and higher
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionUtils.check_permission(permissions, "Allow Trahi to access your location and storage?", 1);






        //add contacts
        addcontacts=(ImageButton)findViewById(R.id.addcontacts);
        addcontacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,AddContacts.class);
                startActivity(intent);
            }
        });

        //nearest policestation
        policebutton=(ImageButton)findViewById(R.id.policebutton);
        policebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentp=new Intent(MainActivity.this,NearestPolice.class);
                startActivity(intentp);


            }
        });

        //nearest ambulance
        ambulancebutton=(ImageButton)findViewById(R.id.ambubutton);
        ambulancebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Under Development...", Toast.LENGTH_SHORT).show();


            }
        });

        //nearest pcr
        pcrbutton=(ImageButton)findViewById(R.id.pcrbutton);
        pcrbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Under Development...", Toast.LENGTH_SHORT).show();


            }
        });

        //safety
        safetybutton=(ImageButton)findViewById(R.id.safetybutton);
        safetybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,SafetyTips.class);
                startActivity(intent);

            }
        });

        //rti process
        rtibutton=(ImageButton)findViewById(R.id.rtibutton);
        rtibutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentrti=new Intent(MainActivity.this,RtiProcess.class);
                startActivity(intentrti);

            }
        });









        //Retrieve Datas
        SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
       // String data_phone_number_1 = (sharedPref.getString("LOCAL_PHONE_NUMBER_0", null));
       // String data_phone_number_2 = (sharedPref.getString("LOCAL_PHONE_NUMBER_1", null));
       // String data_phone_number_3 = (sharedPref.getString("LOCAL_PHONE_NUMBER_2", null));
       // String data_phone_number_4 = (sharedPref.getString("LOCAL_PHONE_NUMBER_3", null));
        String data_name = (sharedPref.getString("LOCAL_NAME", null));












        longitude = " ";
        latitude = " ";
        counter = 0;

        Log.e("longitude", longitude);



        //check if name is null, if its null set its value
        if (data_name == null) {
            permissionUtils.check_permission(permissions, "Allow Trahi to write data?", 2);

            AlertDialog.Builder namegetter = new AlertDialog.Builder(MainActivity.this,R.style.MyAlertDialogStyle);

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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, new LocationListener() {
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {

            }

            @Override
            public void onLocationChanged(final Location location) {

            }
        });
        Location myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (myLocation == null) {
            Toast.makeText(getApplicationContext(), "Please Turn On Your Location", Toast.LENGTH_LONG).show();

        } else {
            myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitude = String.valueOf(myLocation.getLongitude());
            latitude = String.valueOf(myLocation.getLatitude());
            Log.e("longitude", longitude);

        }









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








    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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



