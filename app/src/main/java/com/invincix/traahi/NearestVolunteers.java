package com.invincix.traahi;


import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class NearestVolunteers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_volunteers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.nearestVoltoolbar);
        setSupportActionBar(toolbar);

    }
}
