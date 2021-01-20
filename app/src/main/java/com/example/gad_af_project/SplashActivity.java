package com.example.gad_af_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {

    private ImageView logo_icon;
    private ImageView logo_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        logo_icon = (ImageView) findViewById(R.id.logo_icon);
        logo_text = (ImageView) findViewById(R.id.logo_text);
    }

    @Override
    protected void onStart() {
        super.onStart();

        long animationStartOffset = 300;
        long animationDuration = 500;
        long additionalDelay = 300;
        long timeBeforeMainAct = animationStartOffset + animationDuration + additionalDelay;

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.setRepeatCount(0);
        fadeIn.setStartOffset(animationStartOffset);
        fadeIn.setDuration(animationDuration);

        logo_icon.startAnimation(fadeIn);
        logo_text.startAnimation(fadeIn);

        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }, timeBeforeMainAct);
    }

    public void startAnimation(android.view.View view){
        //RotateAnimation anim = new RotateAnimation(0f, 350f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        fadeIn.setRepeatCount(0);
        fadeIn.setDuration(1000);

        logo_icon.startAnimation(fadeIn);
        logo_text.startAnimation(fadeIn);
    }

    public void stopAnimation(android.view.View view){
        logo_icon.setAnimation(null);
        logo_text.startAnimation(null);
    }
}