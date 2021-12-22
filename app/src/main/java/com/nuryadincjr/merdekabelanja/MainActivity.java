package com.nuryadincjr.merdekabelanja;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISLOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_UID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.activity.AboutActivity;
import com.nuryadincjr.merdekabelanja.activity.LoggedOutActivity;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityMainBinding;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private LocalPreference localPreference;
    private final Handler headlineHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        localPreference = LocalPreference.getInstance(this);
        String uid = localPreference.getPreferences().getString(KEY_UID, "");

        binding.llLogout.setOnClickListener(this::onLogout);
        binding.llAbout.setOnClickListener(this::onOnfo);

        getData(uid);
    }

    private void getData(String uid) {
        Runnable headlineRunnable = () -> new StaffsRepository()
                .getStaffsData(uid)
                .observe(this, this::onDataSet);
        headlineHandler.post(headlineRunnable);
    }

    private void onDataSet(ArrayList<Staffs> staff) {
        if (staff.size() != 0) {
            Staffs staffs = staff.get(0);
            Glide.with(this)
                    .load(staffs.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);
            binding.tvName1.setText(staffs.getName());
            binding.tvDivision.setText(staffs.getDivision());

            binding.tvId.setText(staffs.getUid());
            binding.tvName.setText(staffs.getName());
            binding.tvPhone.setText(staffs.getPhone());
            binding.tvEmail.setText(staffs.getEmail());
            binding.tvAddress.setText(staffs.getAddress());
            binding.tvUsername.setText(staffs.getUsername());
            binding.tvAccount.setText(staffs.getStatus_account());
            binding.tvLatestUpdate.setText(staffs.getLatest_update());
        }
    }

    private void onOnfo(View v) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    private void onLogout(View v) {
        localPreference.getEditor()
                .putInt(NAME_ISLOGIN, 0)
                .putString(NAME_UID, null).apply();
        startActivity(new Intent(this, LoggedOutActivity.class));
        finishAffinity();
    }
}