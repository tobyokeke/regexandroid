package com.gurudeveloperinc.regex;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DELAY_TIME = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //delay the screen and then move to next activity
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent nextActivity = new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(nextActivity);
                        finish();
                    }
                }
        , SPLASH_DELAY_TIME);
    }
}
