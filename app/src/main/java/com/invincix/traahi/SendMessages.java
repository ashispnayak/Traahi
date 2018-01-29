package com.invincix.traahi;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Scanner;

import static com.invincix.traahi.MainActivity.REQUEST_CHECK_SETTINGS;

public class SendMessages extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public String latitude;
    public String longitude;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 2;
    int counter;
    int check = 0;
    String[] data_phone_number=new String[8];
    public TextView sentMessage;
    public ImageView sent, notSent;
    public String data_name, ownNumber, date, flag, count;

    public ProgressDialog load;
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private DatabaseReference counterDB;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_messages);

        longitude = " ";
        latitude = " ";

        if (checkPlayServices()) {

            // Building the GoogleApi client
            buildGoogleApiClient();
        }

        flag = "0";

        load = new ProgressDialog(this);
        load.setCanceledOnTouchOutside(false);
        load.setMessage("Sending Messages...");
        load.show();

        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Intent intent = new Intent(SendMessages.this,LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(),"Please Login To Continue",Toast.LENGTH_LONG).show();
            finish();
        }


        //Retrieve Datas
        SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
        SharedPreferences sharedPrefLogin = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        counter= (sharedPref.getInt("CONTACT_NUMBER",-1));
        Log.e("COunter",String.valueOf(counter));
        for(int i=0;i<=counter;i++) {
            data_phone_number[i]=sharedPref.getString("LOCAL_PHONE_NUMBER_"+String.valueOf(i), null);



        }
        data_name = (sharedPrefLogin.getString("LOCAL_NAME", null));
        ownNumber = (sharedPrefLogin.getString("LOCAL_OWN_NUMBER", null));

        if(ownNumber != null) {
            counterDB = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("Limit");
        }



        sentMessage = (TextView) findViewById(R.id.sentMessageText);

        sent = (ImageView) findViewById(R.id.sent) ;
        notSent = (ImageView) findViewById(R.id.notSent) ;


        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            sentMessage.setText("Please Login to use this feature..");
            load.dismiss();
        }
        else {
                doOperation();
        }




    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiClient.connect();
    }



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
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd/MM/yyyy", cal).toString();
        return date;
    }

    private void displayLocation() {
        LocationManager lm = (LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(gps_enabled) {
            flag = "1";
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
                SharedPreferences sharedPref = getSharedPreferences(MainActivity.STORE_DATA, Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("LATITUDE_SAVE", latitude);
                editor.putString("LONGITUDE_SAVE", longitude);
                editor.apply();

                if (isNetworkAvailable() && longitude != " " && latitude != " " && data_name != null ) {

                    new SendMessageWithTouch().execute(data_phone_number[0], data_phone_number[1], data_phone_number[2], data_phone_number[3], data_phone_number[4], data_phone_number[5], data_phone_number[6], data_phone_number[7], data_name, latitude, longitude);

                } else {
                    produceSnackBar();

                }






            } else {
                //  displayLocationSettingsRequest(getApplicationContext());

                load.dismiss();
                produceSnackBar();

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
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

                            flag = "0";
                            doOperation();
                        }
                        else{
                            sentMessage.setText("Please Login to use this feature");
                        }
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(SendMessages.this, REQUEST_CHECK_SETTINGS);
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
                        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {

                            flag = "0";
                            doOperation();
                        }
                        else{
                            sentMessage.setText("Please Login to use this feature");
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        displayLocationSettingsRequest(mGoogleApiClient);//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }
    private void produceSnackBar(){
        load.dismiss();

        Snackbar.make(findViewById(R.id.activity_send_messages), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                .setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        doOperation();
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
    }

    public void doOperation(){

        if(isNetworkAvailable()) {
            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (gps_enabled) {

                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    counterDB.child("TimeStamp").setValue(ServerValue.TIMESTAMP);

                    counterDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Long timeStamp = (Long) dataSnapshot.child("TimeStamp").getValue();
                            String date_Count = (String) dataSnapshot.child("date").getValue();
                            count = (String) dataSnapshot.child("count").getValue();
                            if (timeStamp != null) {
                                String date = getDate(timeStamp);
                                if (count == null && date_Count == null && flag.equals("0")) {
                                    flag = "1";
                                    Log.e("New","User");
                                    counterDB.child("date").setValue(date);
                                    counterDB.child("count").setValue(String.valueOf(1));
                                    displayLocation();

                                } else if (date_Count != null && count != null && flag.equals("0")) {
                                    try {
                                        Log.e("Inside","Main");
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                        Date date1 = formatter.parse(date);

                                        Date date2 = formatter.parse(date_Count);

                                        if (date1.compareTo(date2) == 0) {
                                            int ctr = Integer.parseInt(count);
                                            if (ctr < 3) {
                                                ctr++;
                                                flag = "1";
                                                counterDB.child("count").setValue(String.valueOf(ctr));
                                                displayLocation();
                                            } else if (ctr >= 3) {
                                                limitReached();
                                                flag = "1";
                                            }

                                        } else if (date1.compareTo(date2) > 0) {
                                            counterDB.child("count").setValue(String.valueOf(1));
                                            counterDB.child("date").setValue(date);
                                            flag = "1";
                                            displayLocation();


                                        }
                                    } catch (ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                }
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
            else{
                displayLocationSettingsRequest(mGoogleApiClient);
            }
        }
        else{
            produceSnackBar();
        }
    }


    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void limitReached() {
        load.dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(SendMessages.this);
        builder.setTitle("Limit Reached");
        builder.setMessage("Sorry for security reasons we provide only sending 3 messages per day");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });
        builder.show();
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
                            +   "\"data\": {\"lat\": \""+latitude+"\",\"long\": \""+longitude+"\",\"name\": \""+data_name+"\",\"number\": \""+ownNumber+"\",\"activity\":\"VictimLocation\"},"
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


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            doOperation();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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
            String message = "I " + data_name + " in trouble at https://maps.google.com/maps?q=" + data_latitude + "," + data_longitude + " Need urgent help and attention.";
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
                sent.setVisibility(View.GONE);
                notSent.setVisibility(View.VISIBLE);
                sendNotifications();
            }


            if (responseCode == 200) {

                android.util.Log.i(TAG, "Response Code = 200.... " + data);
                sent.setVisibility(View.VISIBLE);
                notSent.setVisibility(View.GONE);
                sentMessage.setText("Help is reaching out for you soon!");
                load.dismiss();
                Log.d(TAG, "Messages Sent");

                Toast.makeText(getApplicationContext(), "Messages Sent", Toast.LENGTH_SHORT).show();
                sendNotifications();



            }

        }


    }
}



