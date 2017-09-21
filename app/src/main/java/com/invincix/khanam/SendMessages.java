package com.invincix.khanam;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SendMessages extends AppCompatActivity {
    public String latitude;
    public String longitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    private TextView ph_1,ph_2,ph_3,ph_4,sendingmessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);

        longitude = " ";
        latitude = " ";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission to access the location if missing.
                PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                        Manifest.permission.ACCESS_FINE_LOCATION, true);


                Toast.makeText(getApplicationContext(), "Please Restart the app for the permissions to take effect", Toast.LENGTH_LONG).show();

                Intent startinent = getIntent();
                finish();
                startActivity(startinent);

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
                        SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                        String data_phone_number_1 = (sharedPref.getString("LOCAL_PHONE_NUMBER_1", ""));
                        String data_phone_number_2 = (sharedPref.getString("LOCAL_PHONE_NUMBER_2", ""));
                        String data_phone_number_3 = (sharedPref.getString("LOCAL_PHONE_NUMBER_3", ""));
                        String data_phone_number_4 = (sharedPref.getString("LOCAL_PHONE_NUMBER_4", ""));
                        String data_name = (sharedPref.getString("LOCAL_NAME", ""));
                        ph_1=(TextView)findViewById(R.id.ph_1);
                        ph_2=(TextView)findViewById(R.id.ph_2);
                        ph_3=(TextView)findViewById(R.id.ph_3);
                        ph_4=(TextView)findViewById(R.id.ph_4);
                        sendingmessages=(TextView)findViewById(R.id.sendingmessage);
                        ph_1.setText(data_phone_number_1);
                        ph_2.setText(data_phone_number_2);
                        ph_3.setText(data_phone_number_3);
                        ph_4.setText(data_phone_number_4);




                        if (data_name != null && data_phone_number_1 != null && data_phone_number_2 != null && data_phone_number_3 != null && data_phone_number_4 != null) {
                            new SendMessageWithTouch().execute(data_phone_number_1, data_phone_number_2, data_phone_number_3, data_phone_number_4, data_name, latitude, longitude);
                            Toast.makeText(getApplicationContext(), "Messages Sent", Toast.LENGTH_LONG).show();

                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Messages not Sent", Toast.LENGTH_LONG).show();

                        }
                    }
                }

            }
        }
        else{


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
                    SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                    String data_phone_number_1 = (sharedPref.getString("LOCAL_PHONE_NUMBER_1", ""));
                    String data_phone_number_2 = (sharedPref.getString("LOCAL_PHONE_NUMBER_2", ""));
                    String data_phone_number_3 = (sharedPref.getString("LOCAL_PHONE_NUMBER_3", ""));
                    String data_phone_number_4 = (sharedPref.getString("LOCAL_PHONE_NUMBER_4", ""));
                    String data_name = (sharedPref.getString("LOCAL_NAME", ""));


                    ph_1=(TextView)findViewById(R.id.ph_1);
                    ph_2=(TextView)findViewById(R.id.ph_2);
                    ph_3=(TextView)findViewById(R.id.ph_3);
                    ph_4=(TextView)findViewById(R.id.ph_4);
                    sendingmessages=(TextView)findViewById(R.id.sendingmessage);
                    ph_1.setText(data_phone_number_1);
                    ph_2.setText(data_phone_number_2);
                    ph_3.setText(data_phone_number_3);
                    ph_4.setText(data_phone_number_4);


                    if (data_name != null && data_phone_number_1 != null && data_phone_number_2 != null && data_phone_number_3 != null && data_phone_number_4 != null) {
                        new SendMessageWithTouch().execute(data_phone_number_1, data_phone_number_2, data_phone_number_3, data_phone_number_4, data_name, latitude, longitude);
                        Toast.makeText(getApplicationContext(), "Messages Sent", Toast.LENGTH_LONG).show();

                    }

                }
            }

        }


        }
    }


class SendMessageWithTouch extends AsyncTask<String, Void, String> {


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
