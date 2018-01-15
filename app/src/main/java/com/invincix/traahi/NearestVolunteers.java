package com.invincix.traahi;


import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class NearestVolunteers extends AppCompatActivity {
    private  TextView toolbarText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearest_volunteers);

        Toolbar toolbar = (Toolbar) findViewById(R.id.nearestVoltoolbar);
        setSupportActionBar(toolbar);
        toolbarText = (TextView)  findViewById(R.id.maintoolbartext);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        toolbarText.setTypeface(custom);
        

    }
}
