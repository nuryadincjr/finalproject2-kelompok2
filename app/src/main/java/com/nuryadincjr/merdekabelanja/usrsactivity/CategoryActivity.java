package com.nuryadincjr.merdekabelanja.usrsactivity;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.CollectionPagerAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityCategoryBinding;

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
            onSetData(category);
        }
    }

    private void onSetData(String category) {
        String tabItemFirst = "";
        String tabItemSecond = "";
        int tabCount = 2;

        switch (category) {
            case "Clothing":
                tabItemFirst = "Male";
                tabItemSecond = "Female";
                break;
            case "Electronic":
                tabItemFirst = "Smartphone";
                tabItemSecond = "Computer";
                break;
            case "Book":
                tabCount = 10;
                break;
            case "Other Products":
                tabCount = 1;
                break;
        }

        CollectionPagerAdapter collectionPagerAdapter =
                new CollectionPagerAdapter(this, tabCount, category, binding.tablayout);
        binding.viewpager2.setAdapter(collectionPagerAdapter);

        String finalTabItemFirst = tabItemFirst;
        String finalTabItemSecond = tabItemSecond;
        new TabLayoutMediator(binding.tablayout, binding.viewpager2,
                (tab, position) -> {
                    if(category.equals("Book")){
                        String[] categoryItem = getResources().getStringArray(R.array.book_type);
                        String tabName = categoryItem[position];
                        tab.setText(tabName);
                    } else {
                        if(position==0) tab.setText(finalTabItemFirst);
                        else if(position==1) tab.setText(finalTabItemSecond);
                    }
                }
        ).attach();
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