package com.nuryadincjr.merdekabelanja.usrsactivity;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationBarView;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityUsersBinding;
import com.nuryadincjr.merdekabelanja.usersfragment.CategoryFragment;
import com.nuryadincjr.merdekabelanja.usersfragment.HomeFragment;
import com.nuryadincjr.merdekabelanja.usersfragment.UserProfileFragment;

public class UsersActivity extends AppCompatActivity
        implements NavigationBarView.OnItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        ActivityUsersBinding binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bnvMenu.setOnItemSelectedListener(this);

        if(savedInstanceState == null) getFragmentPage(new HomeFragment());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemHome:
                getFragmentPage(new HomeFragment());
                break;
            case R.id.itemCategorys:
                getFragmentPage(new CategoryFragment());
                break;
            case R.id.itemCart:
//                getFragmentPage(new CartFragment());
                break;
            case R.id.itemAccount:
                getFragmentPage(new UserProfileFragment());
                break;
        }
        return true;
    }

    public void getFragmentPage(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
        }
    }
}