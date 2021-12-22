package com.nuryadincjr.merdekabelanja.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.MainActivity;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adminacitvity.AdminsActivity;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.usrsactivity.UsersActivity;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    private LocalPreference localPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        localPreference = new LocalPreference(this);
        transition();
    }

    private void transition() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            int isLogin = localPreference.getPreferences().getInt("ISLOGIN", 0);
            // user
//            isLogin = 1;
//            localPreference.getEditor().putString("UID", "71DKdinEJVTbs2sORDwnsRzGBqJ3").apply();
            // admin
//            isLogin = 2;
//            localPreference.getEditor().putString("UID", "Ys93PYaUpsgB5kel2tow0RPNnr13").apply();

            Intent intent;
            switch (isLogin) {
                case 1:
                    intent = new Intent(this, UsersActivity.class);
                    break;
                case 2:
                    intent = new Intent(this, AdminsActivity.class);
                    break;
                case 3:
                    intent = new Intent(this, MainActivity.class);
                    break;
                default:
                    intent = new Intent(this, LoggedOutActivity.class);
            }

            startActivity(intent);
            finish();
        }, 3000);
    }
}