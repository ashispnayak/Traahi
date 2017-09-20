package com.invincix.khanam;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private EditText phone_number_1;
    private EditText phone_number_2;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 3000;
    private EditText phone_number_3;
    private EditText phone_number_4;
    private EditText edit_name;
    private Button save;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    public String latitude;
    public int counter;
    public String longitude;
    public static final String STORE_DATA = "MyPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        longitude = " ";
        latitude = " ";
        counter=0;

        Log.e("longitude", longitude);

        //Retrieve Location


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location if missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);

                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    PermissionUtils.requestPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE_REQUEST_CODE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, true);


                }
                Toast.makeText(getApplicationContext(), "Please Restart the app for the permissions to take effect", Toast.LENGTH_LONG).show();



            }
            else {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                {
                    PermissionUtils.requestPermission(MainActivity.this, WRITE_EXTERNAL_STORAGE_REQUEST_CODE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE, true);
                    Toast.makeText(getApplicationContext(), "Please Restart the app for the permissions to take effect", Toast.LENGTH_LONG).show();


                }

                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if(counter>0)
                {
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
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

                    if (longitude != " " && latitude != " ") {
                        //Retrieve Datas
                        SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
                        String data_phone_number_1 = (sharedPref.getString("LOCAL_PHONE_NUMBER_1", ""));
                        String data_phone_number_2 = (sharedPref.getString("LOCAL_PHONE_NUMBER_2", ""));
                        String data_phone_number_3 = (sharedPref.getString("LOCAL_PHONE_NUMBER_3", ""));
                        String data_phone_number_4 = (sharedPref.getString("LOCAL_PHONE_NUMBER_4", ""));
                        String data_name = (sharedPref.getString("LOCAL_NAME", ""));
                        if (data_name != null && data_phone_number_1 != null && data_phone_number_2 != null && data_phone_number_3 != null && data_phone_number_4 != null) {
                            new RequestService().execute(data_phone_number_1, data_phone_number_2, data_phone_number_3, data_phone_number_4, data_name, latitude, longitude);

                        }
                    }
                }

                phone_number_1 = (EditText) findViewById(R.id.phone_number_1);
                phone_number_2 = (EditText) findViewById(R.id.phone_number_2);
                phone_number_3 = (EditText) findViewById(R.id.phone_number_3);
                phone_number_4 = (EditText) findViewById(R.id.phone_number_4);
                edit_name = (EditText) findViewById(R.id.edit_name);
                save = (Button) findViewById(R.id.button_save);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.e("Button", "Clicked");
                        if (data_validation()) {

                            String name = edit_name.getText().toString();
                            String ph_no_1 = phone_number_1.getText().toString();
                            String ph_no_2 = phone_number_2.getText().toString();
                            String ph_no_3 = phone_number_3.getText().toString();
                            String ph_no_4 = phone_number_4.getText().toString();
                            SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.clear();

                            editor.putString("LOCAL_NAME", name);
                            editor.putString("LOCAL_PHONE_NUMBER_1", ph_no_1);
                            editor.putString("LOCAL_PHONE_NUMBER_2", ph_no_2);
                            editor.putString("LOCAL_PHONE_NUMBER_3", ph_no_3);
                            editor.putString("LOCAL_PHONE_NUMBER_4", ph_no_4);
                            editor.apply();
                            Toast.makeText(getApplicationContext(), "Information Saved", Toast.LENGTH_LONG).show();

                        }


                        else {
                            phone_number_1.setText("");
                            phone_number_2.setText("");
                            phone_number_3.setText("");
                            phone_number_4.setText("");
                            edit_name.setText("");

                        }

                    }

                    private boolean data_validation() {
                        boolean valid = true;
                        if (TextUtils.isEmpty(phone_number_1.getText().toString())) {
                            phone_number_1.setError("Required");
                            valid = false;

                        } else {
                            phone_number_1.setError(null);
                        }
                        if (TextUtils.isEmpty(phone_number_2.getText().toString())) {
                            phone_number_2.setError("Required");
                            valid = false;

                        } else {
                            phone_number_2.setError(null);
                        }
                        if (TextUtils.isEmpty(phone_number_3.getText().toString())) {
                            phone_number_3.setError("Required");
                            valid = false;

                        } else {
                            phone_number_3.setError(null);
                        }
                        if (TextUtils.isEmpty(phone_number_4.getText().toString())) {
                            phone_number_4.setError("Required");
                            valid = false;

                        } else {
                            phone_number_4.setError(null);
                        }
                        if (TextUtils.isEmpty(edit_name.getText().toString())) {
                            edit_name.setError("Required");
                            valid = false;

                        } else {
                            edit_name.setError(null);
                        }
                        return valid;
                    }
                });






            }

        } else {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

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

                if (longitude != " " && latitude != " ") {
                    //Retrieve Datas
                    SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
                    String data_phone_number_1 = (sharedPref.getString("LOCAL_PHONE_NUMBER_1", ""));
                    String data_phone_number_2 = (sharedPref.getString("LOCAL_PHONE_NUMBER_2", ""));
                    String data_phone_number_3 = (sharedPref.getString("LOCAL_PHONE_NUMBER_3", ""));
                    String data_phone_number_4 = (sharedPref.getString("LOCAL_PHONE_NUMBER_4", ""));
                    String data_name = (sharedPref.getString("LOCAL_NAME", ""));
                    if (data_name != null && data_phone_number_1 != null && data_phone_number_2 != null && data_phone_number_3 != null && data_phone_number_4 != null) {
                        new RequestService().execute(data_phone_number_1, data_phone_number_2, data_phone_number_3, data_phone_number_4, data_name, latitude, longitude);

                    }
                }
            }


            phone_number_1 = (EditText) findViewById(R.id.phone_number_1);
            phone_number_2 = (EditText) findViewById(R.id.phone_number_2);
            phone_number_3 = (EditText) findViewById(R.id.phone_number_3);
            phone_number_4 = (EditText) findViewById(R.id.phone_number_4);
            edit_name = (EditText) findViewById(R.id.edit_name);
            save = (Button) findViewById(R.id.button_save);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Button", "Clicked");
                    if (data_validation()) {

                        String name = edit_name.getText().toString();
                        String ph_no_1 = phone_number_1.getText().toString();
                        String ph_no_2 = phone_number_2.getText().toString();
                        String ph_no_3 = phone_number_3.getText().toString();
                        String ph_no_4 = phone_number_4.getText().toString();
                        SharedPreferences sharedPref = getSharedPreferences(STORE_DATA, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();

                        editor.putString("LOCAL_NAME", name);
                        editor.putString("LOCAL_PHONE_NUMBER_1", ph_no_1);
                        editor.putString("LOCAL_PHONE_NUMBER_2", ph_no_2);
                        editor.putString("LOCAL_PHONE_NUMBER_3", ph_no_3);
                        editor.putString("LOCAL_PHONE_NUMBER_4", ph_no_4);
                        editor.apply();
                        Toast.makeText(getApplicationContext(), "Information Saved", Toast.LENGTH_LONG).show();

                    }


                    else {
                        phone_number_1.setText("");
                        phone_number_2.setText("");
                        phone_number_3.setText("");
                        phone_number_4.setText("");
                        edit_name.setText("");

                    }

                }

                private boolean data_validation() {
                    boolean valid = true;
                    if (TextUtils.isEmpty(phone_number_1.getText().toString())) {
                        phone_number_1.setError("Required");
                        valid = false;

                    } else {
                        phone_number_1.setError(null);
                    }
                    if (TextUtils.isEmpty(phone_number_2.getText().toString())) {
                        phone_number_2.setError("Required");
                        valid = false;

                    } else {
                        phone_number_2.setError(null);
                    }
                    if (TextUtils.isEmpty(phone_number_3.getText().toString())) {
                        phone_number_3.setError("Required");
                        valid = false;

                    } else {
                        phone_number_3.setError(null);
                    }
                    if (TextUtils.isEmpty(phone_number_4.getText().toString())) {
                        phone_number_4.setError("Required");
                        valid = false;

                    } else {
                        phone_number_4.setError(null);
                    }
                    if (TextUtils.isEmpty(edit_name.getText().toString())) {
                        edit_name.setError("Required");
                        valid = false;

                    } else {
                        edit_name.setError(null);
                    }
                    return valid;
                }
            });


        }




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

class RequestService extends AsyncTask<String, Void, String> {


    private static final String TAG = "PhoneNumbers";
    int responseCode;
    String responseBody;
    private String JsonResponse;

    protected void onPreExecute() {


    }

    @Override
    protected String doInBackground(String... params) {
        String data_phone_number_1 = params[0];
        String data_phone_number_2 = params[1];
        String data_phone_number_3 = params[2];
        String data_phone_number_4 = params[3];
        String data_name = params[4];
        String data_latitude = params[5];
        String data_longitude = params[6];
        String mobiles = data_phone_number_1 + "," + data_phone_number_2 + "," + data_phone_number_3 + "," + data_phone_number_4;
        String message = "I " + data_name + " in trouble at https://maps.google.com/?ie=UTF8&ll=" + data_latitude + "," + data_longitude + " Need urgent help and attention.";
        String authkey = "175066AfkBDqjLjK6e59be06c7";
        String encoded_message = URLEncoder.encode(message);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL("https://control.msg91.com/api/sendhttp.php?authkey=" + authkey + "&mobiles=" + mobiles + "&message=" + encoded_message + "&sender=SAVEME&route=4&country=0");
            urlConnection = (HttpURLConnection) url.openConnection();

//set headers
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.connect();
//0
//get response code
            responseCode = urlConnection.getResponseCode();
            responseBody = urlConnection.getResponseMessage();

            InputStream inputStream = urlConnection.getInputStream();
//input stream
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                //  android.util.Log.e(TAG, "InputStream Is Null");
                return null;

            }
            reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String inputLine;
            while ((inputLine = reader.readLine()) != null)
                buffer.append(inputLine).append("\n");

            if (buffer.length() == 0) {
                //android.util.Log.e(TAG, "Stream was empty. No point in parsing.");
                return null;
            } else {

                JsonResponse = buffer.toString();
//response data
                //android.util.Log.i(TAG, "doInBackGround() " + JsonResponse);
                return JsonResponse;
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    android.util.Log.e(TAG, "Error closing stream", e);
                }
            }
        }
        return null;
    }

    protected void onPostExecute(String data) {

        //android.util.Log.i(TAG, "no return");
        if (data == null) {
            android.util.Log.e(TAG, "Error Null");
        }


        if (responseCode == 200) {

            android.util.Log.i(TAG, "Response Code = 200.... " + data);

            Log.d(TAG, "Messages Sent");


        }

    }


}