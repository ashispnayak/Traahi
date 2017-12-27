package com.invincix.khanam;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;





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
                    Intent intentc = new Intent(Welcome_activity.this,LoginActivity.class);
                    startActivity(intentc);
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



