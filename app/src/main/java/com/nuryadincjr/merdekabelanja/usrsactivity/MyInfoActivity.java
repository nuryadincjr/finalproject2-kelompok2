package com.nuryadincjr.merdekabelanja.usrsactivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityMyInfoBinding;
import com.nuryadincjr.merdekabelanja.models.Users;

public class MyInfoActivity extends AppCompatActivity {

    private ActivityMyInfoBinding binding;
    private Users users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
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
        binding.etName.setText(users.getName());
        binding.etEmail.setText(users.getEmail());
        binding.etOrderAddress.setText(users.getAddress());
        binding.etDestinationAddress.setText(users.getAddress2());
    }
}