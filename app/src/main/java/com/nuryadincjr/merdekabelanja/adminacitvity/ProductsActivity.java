package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_PRODUCT;
import static java.util.Objects.requireNonNull;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityProductsBinding;

public class ProductsActivity extends AppCompatActivity {

    private String[] collect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ActivityProductsBinding binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        collect = getResources().getStringArray(R.array.products_type);

        binding.llClothing.setOnClickListener(v -> getStartActivity(AddClothingActivity.class, collect[0]));
        binding.llElectronic.setOnClickListener(v -> getStartActivity(AddElectronicsActivity.class, collect[1]));
        binding.llBooks.setOnClickListener(v -> getStartActivity(AddBookActivity.class, collect[2]));
        binding.llOther.setOnClickListener(v -> getStartActivity(AddOthersActivity.class, collect[3]));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private <T> void getStartActivity(Class<T> tClass, String sData) {
        startActivity(new Intent(this, tClass)
                .putExtra(NAME_PRODUCT, sData));
    }
}