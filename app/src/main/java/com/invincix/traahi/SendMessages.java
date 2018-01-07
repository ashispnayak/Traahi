package com.invincix.traahi;

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import static com.invincix.traahi.MainActivity.REQUEST_CHECK_SETTINGS;

public class SendMessages extends AppCompatActivity {
    public String latitude;
    public String longitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    int counter;
    int check = 0;
    String[] data_phone_number=new String[8];
    public TextView sentMessage, emergencyToolbarText;
    public ImageView sent, notSent;
    public String data_name;




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
        Log.e("COunter",String.valueOf(counter));
        for(int i=0;i<=counter;i++) {
            data_phone_number[i]=sharedPref.getString("LOCAL_PHONE_NUMBER_"+String.valueOf(i), null);



        }
         data_name = (sharedPrefLogin.getString("LOCAL_NAME", null));

        sentMessage = (TextView) findViewById(R.id.sentMessageText);
        emergencyToolbarText = (TextView) findViewById(R.id.emergencytoolbartext);
        sent = (ImageView) findViewById(R.id.sent) ;
        notSent = (ImageView) findViewById(R.id.notSent) ;
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        emergencyToolbarText.setTypeface(typeface);

        displayLocation();







        }

    private void displayLocation() {
        Log.e("Inside","Display Location");

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        boolean gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
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
            if (isNetworkAvailable()) {
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

                    sent.setVisibility(View.GONE);
                    notSent.setVisibility(View.VISIBLE);
                    displayLocation();


                } else {
                    myLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    longitude = String.valueOf(myLocation.getLongitude());
                    latitude = String.valueOf(myLocation.getLatitude());
                    Log.e("longitude", longitude);

                    if (isNetworkAvailable()) {
                        if (longitude != " " && latitude != " ") {
                            if (data_name != null && data_phone_number[0] != null && data_phone_number[1] != null && data_phone_number[2] != null && data_phone_number[3] != null) {
                                sent.setVisibility(View.VISIBLE);
                                notSent.setVisibility(View.GONE);
                                sentMessage.setText("Emergency Messages Have Been Sent...");
                                sendNotifications();
                               // new SendMessageWithTouch().execute(data_phone_number[0], data_phone_number[1], data_phone_number[2], data_phone_number[3], data_phone_number[4], data_phone_number[5], data_phone_number[6], data_phone_number[7], data_name, latitude, longitude);
                                Toast.makeText(getApplicationContext(), "Messages Sent", Toast.LENGTH_LONG).show();

                            } else {
                                sent.setVisibility(View.GONE);
                                notSent.setVisibility(View.VISIBLE);
                                sentMessage.setText("Provide atleast 4 numbers");
                                Toast.makeText(getApplicationContext(), "Provide atleast 4 numbers", Toast.LENGTH_LONG).show();

                            }

                        }
                    } else {
                        produceSnackBar();

                    }


                }
            } else {
                produceSnackBar();
            }
        }
        else{
            displayLocationSettingRequest(getApplicationContext());

        }


    }
    private void produceSnackBar(){
        Snackbar.make(findViewById(R.id.activity_send_messages), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    displayLocation();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    private void displayLocationSettingRequest(Context context) {
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
                        Log.e("Success","");
                        displayLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:


                        try {
                            // Show the dialog by calling startResolutionForResult(), and check the result
                            // in onActivityResult().
                            status.startResolutionForResult(SendMessages.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e("Not Success","");
                        break;
                }
            }

        });
       produceSnackBar();


    }

    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void sendNotifications() {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String jsonResponse;

                    URL url = new URL("https://onesignal.com/api/v1/notifications");
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setUseCaches(false);
                    con.setDoOutput(true);
                    con.setDoInput(true);

                    con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    con.setRequestProperty("Authorization", "Basic MzQ1MzhlMTgtMDVlNS00Y2YyLTlkNjAtZjBjMTM3ODViYmNi");
                    con.setRequestMethod("POST");

                    String strJsonBody = "{"
                            +   "\"app_id\": \"833c1a76-5059-44ad-9cff-2a7909cc027d\","
                            +   "\"filters\": [{\"field\": \"tag\", \"key\": \"isaVolunteer\", \"relation\": \"=\", \"value\": \"Yes\"},{\"field\": \"location\", \"radius\": \"10000\",\"lat\": \"" +latitude+ "\",\"long\": \"" +longitude+"\"}],"
                            +   "\"data\": {\"lat\": \""+latitude+"\",\"long\": \""+longitude+"\",\"activity\":\"VictimLocation\"},"
                            +   "\"contents\": {\"en\": \"Help! Someone is in danger. Get the location\"}"
                            + "}";


                    System.out.println("strJsonBody:\n" + strJsonBody);

                    byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                    con.setFixedLengthStreamingMode(sendBytes.length);

                    OutputStream outputStream = con.getOutputStream();
                    outputStream.write(sendBytes);

                    int httpResponse = con.getResponseCode();
                    System.out.println("httpResponse: " + httpResponse);

                    if (  httpResponse >= HttpURLConnection.HTTP_OK
                            && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                        Log.e("Notifications","Sent");
                        Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        scanner.close();
                    }
                    else {
                        Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                        jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                        Log.e("Notifications","NOT Sent");
                        scanner.close();
                    }
                    System.out.println("jsonResponse:\n" + jsonResponse);

                } catch(Throwable t) {
                    t.printStackTrace();
                }

            }
        });

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
