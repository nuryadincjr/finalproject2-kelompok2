package com.nuryadincjr.merdekabelanja.usrsactivity;


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

public class UsersActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    private ActivityUsersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        binding = ActivityUsersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.bnvMenu.setOnItemSelectedListener(this);

        if(savedInstanceState == null) {
            getFragmentPage(new HomeFragment());
        }
    }

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

    public boolean getFragmentPage(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.flContainer, fragment)
                    .commit();
            return true;
        }
        return true;
    }
}