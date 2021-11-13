package com.nuryadincjr.merdekabelanja.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.nuryadincjr.merdekabelanja.MainActivity;
import com.nuryadincjr.merdekabelanja.R;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        transition();
    }

    private void transition() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            startActivity(new Intent(this, LoggedOutActivity.class));
            finish();
        }, 3000);
    }
}