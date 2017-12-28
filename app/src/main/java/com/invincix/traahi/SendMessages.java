package com.invincix.traahi;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

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
    int counter;
    String[] data_phone_number=new String[8];
    private TextView ph_1, ph_2, ph_3, ph_4, ph_5,ph_6,ph_7,ph_8,sendingmessages;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);

        longitude = " ";
        latitude = " ";
        //Retrieve Datas
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
        SharedPreferences sharedPrefLogin = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        counter= (sharedPref.getInt("CONTACT_NUMBER",-1));
        for(int i=0;i<=counter;i++) {
            data_phone_number[i]=sharedPref.getString("LOCAL_PHONE_NUMBER_"+String.valueOf(i), null);



        }
        String data_name = (sharedPrefLogin.getString("LOCAL_NAME", null));
        ph_1 = (TextView) findViewById(R.id.ph_1);
        ph_2 = (TextView) findViewById(R.id.ph_2);
        ph_3 = (TextView) findViewById(R.id.ph_3);
        ph_4 = (TextView) findViewById(R.id.ph_4);
        ph_5 = (TextView) findViewById(R.id.ph_5);
        ph_6 = (TextView) findViewById(R.id.ph_6);
        ph_7 = (TextView) findViewById(R.id.ph_7);
        ph_8 = (TextView) findViewById(R.id.ph_8);

        sendingmessages = (TextView) findViewById(R.id.sendingmessage);
        ph_1.setText(data_phone_number[0]);
        ph_2.setText(data_phone_number[1]);
        ph_3.setText(data_phone_number[2]);
        ph_4.setText(data_phone_number[3]);
        ph_5.setText(data_phone_number[4]);
        ph_6.setText(data_phone_number[5]);
        ph_7.setText(data_phone_number[6]);
        ph_8.setText(data_phone_number[7]);

        sendingmessages.setText("Hii " + data_name + " sending messages");


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

                if (longitude != " " && latitude != " ") {







                    if (data_name != null && data_phone_number[0] != null && data_phone_number[1] != null && data_phone_number[2] != null && data_phone_number[3] != null) {
                        new SendMessageWithTouch().execute(data_phone_number[0], data_phone_number[1],data_phone_number[2],data_phone_number[3],data_phone_number[4],data_phone_number[5],data_phone_number[6],data_phone_number[7],data_name, latitude, longitude);
                        Toast.makeText(getApplicationContext(), "Messages Sent", Toast.LENGTH_LONG).show();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Provide atleast 4 numbers", Toast.LENGTH_LONG).show();

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
        String data_phone_number_5 = params[4];
        String data_phone_number_6 = params[5];
        String data_phone_number_7 = params[6];
        String data_phone_number_8 = params[7];

        String data_name = params[8];
        String data_latitude = params[9];
        String data_longitude = params[10];
        String mobiles = data_phone_number_1 + "," + data_phone_number_2 + "," + data_phone_number_3 + "," + data_phone_number_4 + "," + data_phone_number_5 + "," + data_phone_number_6 + "," + data_phone_number_7 + "," + data_phone_number_8;
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