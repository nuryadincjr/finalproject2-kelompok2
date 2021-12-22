package com.nuryadincjr.merdekabelanja.adminacitvity;

import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.navigation.NavigationView;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.activity.AboutActivity;
import com.nuryadincjr.merdekabelanja.activity.SettingsActivity;
import com.nuryadincjr.merdekabelanja.adminfragment.DashboardFragment;
import com.nuryadincjr.merdekabelanja.adminfragment.ProductsFragment;
import com.nuryadincjr.merdekabelanja.adminfragment.StaffsFragment;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAdminsBinding;

public class AdminsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private ActivityAdminsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins);

        binding = ActivityAdminsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        new ActionBarDrawerToggle(this,
                binding.drawerLayout, binding.toolbar,
                R.string.navigation_open, R.string.navigation_close).syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState == null) {
            binding.navigationView.setCheckedItem(R.id.itemDashboard);
            getFragmentPage(new DashboardFragment());
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemDashboard:
                getFragmentPage(new DashboardFragment());
                requireNonNull(getSupportActionBar()).setTitle("Dashboard");
                break;
            case R.id.itemStaffs:
                getFragmentPage(new StaffsFragment());
                break;
            case R.id.itemProducts:
                getFragmentPage(new ProductsFragment());
                break;
            case R.id.itemUsers:
                getFragmentPage(new StaffsFragment.UsersFragment());
                break;
            case R.id.itemSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.itemAbouts:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
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