package com.invincix.khanam;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.util.Pair;
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
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.victor.loading.newton.NewtonCradleLoading;

import java.util.ArrayList;


public class SafetyTips extends AppCompatActivity {

    private static RecyclerView recyclerView;
    private static ArrayList<SafetyTipsFeed> data;
    private DatabaseReference mDatabase;
    private NewtonCradleLoading newtonCradleLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_tips);
        newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.progressBarFeed);
        int col = Color.parseColor("#cf3025");
        newtonCradleLoading.setLoadingColor(col);
        newtonCradleLoading.setVisibility(View.VISIBLE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarsafetytips);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //set toolbar text
        TextView toolbarText = (TextView) findViewById(R.id.toolbartext_tips);
        Typeface custom = Typeface.createFromAsset(getAssets(), "fonts/toolbarfont.ttf");
        toolbarText.setTypeface(custom);


        mDatabase = FirebaseDatabase.getInstance().getReference().child("Safety");

        recyclerView = (RecyclerView) findViewById(R.id.safetycontent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        checkInternet();




    }

    public void checkInternet()
    {

        if(isNetworkAvailable())
        {

            newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.progressBarFeed);

            Log.e("FOund","Internet");
            newtonCradleLoading.start();
            newtonCradleLoading.setVisibility(View.VISIBLE);

            populatefeed();

        }
        else{
            recyclerView.setVisibility(View.GONE);
            newtonCradleLoading.setVisibility(View.GONE);
            Snackbar.make(findViewById(R.id.safetytipslayout), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            checkInternet();


                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();

        }

    }






    public  boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void populatefeed(){


        FirebaseRecyclerAdapter<SafetyTipsFeed,CustomAdapter> firebaseRecyclerAdapter= new FirebaseRecyclerAdapter<SafetyTipsFeed, CustomAdapter>(

                SafetyTipsFeed.class,
                R.layout.safetytipsfeed,
                CustomAdapter.class,
                mDatabase

        ) {
            @Override
            protected void populateViewHolder(CustomAdapter viewHolder, final SafetyTipsFeed model, int position) {
                newtonCradleLoading = (NewtonCradleLoading) findViewById(R.id.progressBarFeed);

                if (newtonCradleLoading.isStart()) {

                    newtonCradleLoading.setVisibility(View.GONE);
                }


                final String title = model.getName();
                final String descr = model.getDescription();
                final String mainpic = model.getUrl();
                final String coverpic = model.getCoverImg();
                viewHolder.setName(model.getName());
                viewHolder.setDescription(model.getDescription());
                viewHolder.setUrl(getApplicationContext() , model.getUrl());
                viewHolder.setCoverImg(getApplicationContext(), model.getCoverImg());
                viewHolder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        Intent intent = new Intent(SafetyTips.this,SafetyTipsDetails.class);
                        Bundle extras = new Bundle();
                        extras.putString("title",title);
                        extras.putString("cover",coverpic);
                        extras.putString("description",descr);
                        extras.putString("mainpic",mainpic);
                        intent.putExtras(extras);
                        startActivity(intent);
                    }
                });
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
            postimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            Picasso.with(c).load(url).into(postimage);

        }
        public void setCoverImg(Context c, String Url){
            RoundedImageView cover = (RoundedImageView) mview.findViewById(R.id.img_cover_d);
            Picasso.with(c).load(Url).into(cover);


        }

    }

}







