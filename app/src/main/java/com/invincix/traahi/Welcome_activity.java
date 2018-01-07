package com.invincix.traahi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;


public class Welcome_activity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activity);




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



