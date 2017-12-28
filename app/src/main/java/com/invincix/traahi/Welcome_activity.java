package com.invincix.traahi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;


public class Welcome_activity extends AppCompatActivity {

    public static final String STORE_DATABASE_CHECK = "CHECK";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activity);

        SharedPreferences sharedPref = getSharedPreferences(STORE_DATABASE_CHECK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("DATABASE_CHECK", "0");
        editor.apply();

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(FirebaseAuth.getInstance().getCurrentUser()==null){
                        Intent intentc = new Intent(Welcome_activity.this,LoginActivity.class);
                        startActivity(intentc);
                    }
                    else{
                        Intent intentb = new Intent(Welcome_activity.this,MainActivity.class);
                        startActivity(intentb);
                    }

                }
            }
        };
        timerThread.start();
    }

    @Override
    protected void onPause() {

        super.onPause();
        finish();
    }

}



