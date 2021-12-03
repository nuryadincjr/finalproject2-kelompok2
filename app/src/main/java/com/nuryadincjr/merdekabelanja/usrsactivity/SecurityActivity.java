package com.nuryadincjr.merdekabelanja.usrsactivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivitySecurityBinding;
import com.nuryadincjr.merdekabelanja.models.Users;

public class SecurityActivity extends AppCompatActivity {
    private ActivitySecurityBinding binding;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        users = getIntent().getParcelableExtra("DATA");

        onDataSet(users);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDataSet(Users users) {
        binding.etPhone.setText(users.getPhone());
        binding.etPassword.setText(users.getPassword());
        binding.etConfPassword.setText(users.getPassword());
    }
}