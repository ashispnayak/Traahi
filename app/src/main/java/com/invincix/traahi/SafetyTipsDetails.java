package com.invincix.traahi;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class SafetyTipsDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safety_tips_details);

        final Intent intent = getIntent();
        CardView cardView = (CardView) findViewById(R.id.card_view);
        ImageView cover = (ImageView) findViewById(R.id.cover_details);
        TextView safetitle = (TextView) findViewById(R.id.txt_safety_title);
        ImageView mainpic = (ImageView) findViewById(R.id.cover_bg_details);
        TextView details = (TextView) findViewById(R.id.txt_safety_details);
        TextView goBackButton = (TextView) findViewById(R.id.goBackButton);
        TextView safetyTag = (TextView) findViewById(R.id.safetytag);
        Typeface typeface = Typeface.createFromAsset(getAssets(),"fonts/fontawesome-webfont.ttf");
        goBackButton.setTypeface(typeface);

        final Bundle extras = intent.getExtras();

        Picasso.with(this).load(extras.getString("mainpic")).into(mainpic);
        Picasso.with(this).load(extras.getString("cover")).into(cover);
        safetitle.setText(extras.getString("title"));
        details.setText(extras.getString("description"));

        if(extras.getString("Author")!=null) {
            safetyTag.setText(extras.getString("Author"));
        }





        goBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String activityOpen = extras.getString("activity");
                if(activityOpen.equals("SafetyTips") ) {
                    Intent intent = new Intent(SafetyTipsDetails.this, SafetyTips.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Intent intent = new Intent(SafetyTipsDetails.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });


        //These are lines helping Details_Card To Animate
        //===============================================
        AnimatorSet animationSet = new AnimatorSet();

        //Translating Details_Card in Y Scale
        ObjectAnimator card_y = ObjectAnimator.ofFloat(cardView, View.TRANSLATION_Y, 70);
        card_y.setDuration(2500);
        card_y.setRepeatMode(ValueAnimator.REVERSE);
        card_y.setRepeatCount(ValueAnimator.INFINITE);
        card_y.setInterpolator(new LinearInterpolator());

        //Translating Movie_Cover in Y Scale
        ObjectAnimator cover_y = ObjectAnimator.ofFloat(cover, View.TRANSLATION_Y, 30);
        cover_y.setDuration(3000);
        cover_y.setRepeatMode(ValueAnimator.REVERSE);
        cover_y.setRepeatCount(ValueAnimator.INFINITE);
        cover_y.setInterpolator(new LinearInterpolator());

        animationSet.playTogether(card_y,cover_y);
        animationSet.start();




    }
}
