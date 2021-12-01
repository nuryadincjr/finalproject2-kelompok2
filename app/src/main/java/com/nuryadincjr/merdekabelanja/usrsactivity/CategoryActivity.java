package com.nuryadincjr.merdekabelanja.usrsactivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityCategoryBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private ActivityCategoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String category = getIntent().getStringExtra("ISCATEGORY");
        getSupportActionBar().setTitle(category);

        if(savedInstanceState == null) {
            getData(category);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(String category) {
        MainViewModel mainViewModel = new MainViewModel(getApplication());
        mainViewModel.getFilterProductsLiveData(
                new String[]{category}).observe(this, products -> {
            List<Products> productsList = new ArrayList<>(products);
            ProductsAdapter productsAdapter = new ProductsAdapter(1, productsList);
            binding.rvProducts.setAdapter(productsAdapter);
            binding.rvProducts.setItemAnimator(new DefaultItemAnimator());

            onListener(productsAdapter, productsList);
        });
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getApplicationContext(),
                        productsList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(),
                        productsList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}