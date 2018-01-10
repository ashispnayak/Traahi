package com.invincix.traahi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

public class TraahiVolunteer extends AppCompatActivity {

    private TextView volunteerToolbarText, volunteerStatus, volunteerStatusShow;
    private Button volunteerStatusButton, nearbyVolunteers;
    private DatabaseReference volunteerDatabase, volMainDatabase;
    private String ownNumber, volunteerStat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traahi_volunteer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.volunteertoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //set toolbar text
        volunteerToolbarText = (TextView) findViewById(R.id.volunteertoolbartext);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        volunteerToolbarText.setTypeface(custom);

        volunteerStatusShow = (TextView) findViewById(R.id.volunteerStatusShow);

        //Retrieve Data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        volunteerStat = extras.getString("volunteerStatus");
        volunteerStatusButton = (Button) findViewById(R.id.volunteerStatusButton);
        if(volunteerStat.equals("Yes")){
            volunteerStatusShow.setText("You are a volunteer");
            volunteerStatusShow.setBackgroundColor(Color.parseColor("#339900"));
            volunteerStatusButton.setText("Opt out from Traahi Volunteer");
        }
        else{
            volunteerStatusShow.setText("You are not a volunteer");
            volunteerStatusShow.setBackgroundColor(Color.parseColor("#cf3025"));
            volunteerStatusButton.setText("Opt In for Traahi Volunteer");
        }


        volunteerStatus = (TextView) findViewById(R.id.volunteerStatus);
        volunteerStatus.setTypeface(custom);

        SharedPreferences sharedPrefContact = getSharedPreferences(LoginActivity.STORE_DATA_NAME, Context.MODE_PRIVATE);
        ownNumber = sharedPrefContact.getString("LOCAL_OWN_NUMBER", null);


        volunteerDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(ownNumber).child("isaVolunteer");
        volMainDatabase = FirebaseDatabase.getInstance().getReference().child("Volunteers");






        volunteerStatusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Hey",volunteerStat);

                if (isNetworkAvailable() ) {
                    if (volunteerStat.equals("Yes")) {
                        volunteerDatabase.setValue("No");
                        volMainDatabase.child(ownNumber).removeValue();
                        volunteerStat = "No";
                        OneSignal.sendTag("isaVolunteer", "No");
                        volunteerStatusShow.setText("You are not a volunteer");
                        volunteerStatusShow.setBackgroundColor(Color.parseColor("#cf3025"));
                        volunteerStatusButton.setText("Opt In for Traahi Volunteer");
                    } else if (volunteerStat.equals("No")) {
                        volunteerDatabase.setValue("Yes");
                        volunteerStat = "Yes";
                        volMainDatabase.child(ownNumber).setValue("Profile");
                        OneSignal.sendTag("isaVolunteer", "Yes");
                        volunteerStatusShow.setText("You are a volunteer");
                        volunteerStatusShow.setBackgroundColor(Color.parseColor("#339900"));
                        volunteerStatusButton.setText("Opt out from Traahi Volunteer");
                    }
                }
                else{
                    Snackbar.make(findViewById(R.id.activity_traahi_volunteer), "No Internet Connection", Snackbar.LENGTH_SHORT)
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



    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        volunteerDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                volunteerStat = (String) dataSnapshot.getValue();
                if(volunteerStat!=null){
                    Log.e("data",volunteerStat);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
