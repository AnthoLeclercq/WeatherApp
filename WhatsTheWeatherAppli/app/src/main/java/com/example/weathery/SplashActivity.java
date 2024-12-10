package com.example.weathery;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    private Animation topAnim,bottomAnim;
//sun_image
    private ImageView cloud_one_image,cloud_two_image,cloud_three_image;
    private TextView logo,slogan;

    private static int DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //sun_image = findViewById(R.id.sun);
        cloud_one_image = findViewById(R.id.cloud1);
        cloud_two_image = findViewById(R.id.cloud2);
        cloud_three_image = findViewById(R.id.cloud3);
        logo = findViewById(R.id.logo_txt);
        slogan = findViewById(R.id.slogan);

        Context context;
        topAnim = AnimationUtils.loadAnimation(this, R.anim.top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        //sun_image.setAnimation(topAnim);
        cloud_one_image.setAnimation(bottomAnim);
        cloud_two_image.setAnimation(topAnim);
        cloud_three_image.setAnimation(bottomAnim);
        logo.setAnimation(bottomAnim);
        slogan.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },DELAY);
    }
}