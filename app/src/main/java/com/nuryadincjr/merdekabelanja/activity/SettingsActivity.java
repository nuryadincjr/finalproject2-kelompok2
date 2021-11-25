package com.nuryadincjr.merdekabelanja.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adminacitvity.AdminsProfileActivity;
import com.nuryadincjr.merdekabelanja.databinding.ActivitySettingsBinding;
import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private LocalPreference localPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        localPreference = LocalPreference.getInstance(this);

        binding.btnLogout.setOnClickListener(v -> {
            localPreference.getEditor()
                    .putInt("ISLOGIN", 0)
                    .putString("UID", null).apply();
            startActivity(new Intent(this, LoggedOutActivity.class));
        });

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Admins admins = new Admins();
        admins.setUid(auth.getUid());

        binding.tvProfile.setOnClickListener(v -> startActivity(new
                Intent(this, AdminsProfileActivity.class)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}