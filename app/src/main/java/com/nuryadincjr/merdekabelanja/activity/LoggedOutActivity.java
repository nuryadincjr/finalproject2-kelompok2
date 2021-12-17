package com.nuryadincjr.merdekabelanja.activity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISLOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_LOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_REGISTER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityLoggedOutBinding;

public class LoggedOutActivity extends AppCompatActivity {

    protected ActivityLoggedOutBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_out);
        binding = ActivityLoggedOutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getItemUser(binding.btnLogin);
        getItemUser(binding.btnRegister);

        getItem(binding.tvLoginAdmin);
        getItem(binding.tvLoginStaff);
        getItem(binding.tvAbout);
    }

    @SuppressLint("NonConstantResourceId")
    private void getItem(TextView p) {
        p.setOnClickListener(v -> {
            switch (p.getId()){
                case R.id.tvLoginAdmin:
                    onClick(LoginActivity.class, NAME_ISLOGIN, "ADMIN");
                    break;
                case R.id.tvLoginStaff:
                    onClick(LoginActivity.class, NAME_ISLOGIN, "STAFF");
                    break;
                case R.id.tvAbout:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    private void getItemUser(Button b) {
        b.setOnClickListener(v -> {
            switch (b.getId()){
                case R.id.btnLogin:
                    onClick(LoginActivity.class,NAME_LOGIN, "USER");
                    break;
                case R.id.btnRegister:
                    onClick(RegisterActivity.class,NAME_REGISTER, "USER");
                    break;
            }
        });
    }

    private <T> void onClick(Class<T> tClass, String key, String value) {
        startActivity(new Intent(this, tClass)
                .putExtra(key, value));
    }
}