package com.example.kirill.techpark16.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.kirill.techpark16.R;



/**
 * Created by kirill on 25.05.16.
 */
public class SplashScreen extends Activity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashScreen.this,ActivityBase.class);
                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
