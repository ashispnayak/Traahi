package com.invincix.khanam;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;


public class SafetyTips extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static ArrayList<SafetyTipsFeed> data;
    private ProgressDialog mProgressDialog;
    private DatabaseReference mDatabase;
    private ProgressDialog progressDialog;
    private TextView textView_NetworkErrorIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_tips);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsafetytips);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //set toolbar text
        TextView toolbarText = (TextView) findViewById(R.id.toolbartext_tips);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        toolbarText.setTypeface(custom);

        textView_NetworkErrorIcon = (TextView) findViewById(R.id.textView_NetworkError);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Safety");

        recyclerView = (RecyclerView) findViewById(R.id.safetycontent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        show();


    }

    public void show()
    {
        if(isNetworkAvailable())
        {
            ProgressBar mProgress=(ProgressBar)findViewById(R.id.progressBarFeed) ;

            Log.e("FOund","Internet");
            mProgress.setVisibility(View.VISIBLE);
            populatefeed();

        }
        else{
            recyclerView.setVisibility(View.GONE);
            Snackbar.make(findViewById(R.id.safetytipslayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mProgressDialog=new ProgressDialog(SafetyTips.this);
                            mProgressDialog.setMessage("Retrying...");
                            mProgressDialog.show();
                            show();


                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
        }

    }






    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void showMessageDialog(String title, String message) {


        AlertDialog.Builder builderSingle;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            builderSingle = new AlertDialog.Builder(SafetyTips.this, R.style.MyAlertDialogStyle);
        } else {
            builderSingle = new AlertDialog.Builder(SafetyTips.this);
        }

        builderSingle.setMessage(message);
        builderSingle.setTitle(title);
        builderSingle.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builderSingle.show();

    }

    public void populatefeed(){


        FirebaseRecyclerAdapter<SafetyTipsFeed,CustomAdapter> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<SafetyTipsFeed, CustomAdapter>(

                SafetyTipsFeed.class,
                R.layout.safetytipsfeed,
                CustomAdapter.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(CustomAdapter viewHolder, SafetyTipsFeed model, int position) {
                ProgressBar mProgress=(ProgressBar)findViewById(R.id.progressBarFeed) ;

                mProgress.setVisibility(View.GONE);

                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setUrl(getApplicationContext() , model.getUrl());

            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public  static class CustomAdapter extends RecyclerView.ViewHolder{

        View mview;

        public CustomAdapter(View itemView) {
            super(itemView);
            mview=itemView;

        }

        public void setName(String title){
            TextView posttitle =(TextView) mview.findViewById(R.id.safetytitle);
            posttitle.setText(title);

        }
        public void setDescription(String Description){
            TextView postdescription= (TextView) mview.findViewById(R.id.safetydescription);
            postdescription.setText(Description);
        }
        public void setUrl(Context c,String url){
            ImageView postimage= (ImageView) mview.findViewById(R.id.safetyimage);
            Picasso.with(c).load(url).into(postimage);

        }

    }

}







