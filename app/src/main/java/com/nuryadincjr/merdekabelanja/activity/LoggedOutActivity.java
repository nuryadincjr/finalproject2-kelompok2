package com.nuryadincjr.merdekabelanja.activity;

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

    private void getItem(TextView p) {
        p.setOnClickListener(v -> {
            switch (p.getId()){
                case R.id.tvLoginAdmin:
                    startActivity(new Intent(this, LoginActivity.class)
                            .putExtra("LOGIN", "ADMIN"));
                    break;
                case R.id.tvLoginStaff:
                    startActivity(new Intent(this, LoginActivity.class)
                            .putExtra("LOGIN", "STAFF"));
                    break;
                case R.id.tvAbout:
                    startActivity(new Intent(this, AboutActivity.class));
                    break;
            }
        });
    }

    private void getItemUser(Button b) {
        b.setOnClickListener(v -> {
            switch (b.getId()){
                case R.id.btnLogin:
                    startActivity(new Intent(this, LoginActivity.class)
                            .putExtra("LOGIN", "USER"));
                    break;
                case R.id.btnRegister:
                    startActivity(new Intent(this, RegisterActivity.class)
                            .putExtra("REGISTER", "USER"));
                    break;
            }
        });
    }
}