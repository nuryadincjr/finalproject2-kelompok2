package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityProductsBinding;

public class ProductsActivity extends AppCompatActivity {
    private ActivityProductsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.llClothing.setOnClickListener(v -> {
            startActivity(new Intent(this, AddClothingActivity.class)
                    .putExtra("PRODUCT", "Colething"));
        });

        binding.llBooks.setOnClickListener(v -> {
            startActivity(new Intent(this, AddBookActivity.class)
                    .putExtra("PRODUCT", "Book"));
        });

        binding.llElectronic.setOnClickListener(v -> {
            startActivity(new Intent(this, AddElectronicsActivity.class)
                    .putExtra("PRODUCT", "Electronic"));
        });

        binding.llOther.setOnClickListener(v -> {
            startActivity(new Intent(this, AddOthersActivity.class)
                    .putExtra("PRODUCT", "Other Products"));
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}