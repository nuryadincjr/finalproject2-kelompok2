package com.nuryadincjr.merdekabelanja.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String isLogin = getIntent().getStringExtra("LOGIN");

        switch (isLogin) {
            case "USER":
                binding.btnLogin.setText("LOG IN");
                break;
            case "ADMIN":
            case "STAFF":
                binding.btnLogin.setText("LOG IN AS " + isLogin);
                break;
        }




    }
}