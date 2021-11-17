package com.nuryadincjr.merdekabelanja.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivitySettingsBinding;
import com.nuryadincjr.merdekabelanja.util.LocalPreference;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private LocalPreference localPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        localPreference = LocalPreference.getInstance(this);

        binding.btnLogout.setOnClickListener(v -> {
            localPreference.getEditor().putInt("ISLOGIN", 0).apply();
            startActivity(new Intent(this, LoggedOutActivity.class));
        });
    }
}