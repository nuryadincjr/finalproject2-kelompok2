package com.nuryadincjr.merdekabelanja.usrsactivity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_CATEGORY;

import static java.util.Objects.*;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.CollectionPagerAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityCategoryBinding;
import com.nuryadincjr.merdekabelanja.resorces.Categoryes;

public class CategoryActivity extends AppCompatActivity {
    private ActivityCategoryBinding binding;
    private Categoryes categoryes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        binding = ActivityCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        categoryes = Categoryes.getInstance(this);
        String category = getIntent().getStringExtra(NAME_CATEGORY);
        getSupportActionBar().setTitle(category);

        if(savedInstanceState == null) onDataSet(category);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDataSet(String category) {
        int tabCount = 0;
        switch (category) {
            case "Clothing":
                tabCount = categoryes.people().length;
                break;
            case "Electronic":
                tabCount = categoryes.electronicType().length;
                break;
            case "Book":
                tabCount = categoryes.bookType().length;
                break;
            case "Other Products":
                tabCount = 1;
                break;
        }

        CollectionPagerAdapter collectionPagerAdapter =
                new CollectionPagerAdapter(this, tabCount, category, binding.tabLayout);
        binding.viewpager2.setAdapter(collectionPagerAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewpager2,
                (tab, position) -> {
                    switch (category) {
                        case "Book":
                            tab.setText(categoryes.bookType()[position]);
                            break;
                        case "Clothing":
                            tab.setText(categoryes.people()[position]);
                            break;
                        case "Electronic":
                            tab.setText(categoryes.electronicType()[position]);
                            break;
                    }
                }
        ).attach();
    }
}