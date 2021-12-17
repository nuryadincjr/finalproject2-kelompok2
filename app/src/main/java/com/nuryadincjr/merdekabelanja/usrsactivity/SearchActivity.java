package com.nuryadincjr.merdekabelanja.usrsactivity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivitySearchBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchMenuItem = menu.findItem( R.id.itemSearch );
        searchMenuItem.expandActionView();
        searchMenuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return false;
            }
        });
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        SearchView searchView = (SearchView) menu.findItem(R.id.itemSearch).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("Search");
        searchView.onActionViewExpanded();
        searchView.setFocusable(true);
        searchView.requestFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getData(s, 1);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getData(s, 2);
                return false;
            }
        });
        return super.onPrepareOptionsMenu(menu);
    }

    private void getData(String s, int session) {
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getSearchProducts(s).observe(this, products -> {
            if(products.size() != 0) {
                List<Products> productsList = new ArrayList<>(products);
                ProductsAdapter productsAdapter = new ProductsAdapter(session, productsList);

                int viewWidth = binding.rvSearching.getMeasuredWidth();
                int spanCoutnt = (int) Math.floor(viewWidth / 360f);

                if(session == 2) spanCoutnt = 1;

                binding.rvSearching.setLayoutManager(new GridLayoutManager(this, spanCoutnt));
                binding.rvSearching.setAdapter(productsAdapter);

                onListener(productsAdapter, productsList);
            }
        });
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(getApplicationContext(), DetailItemProductActivity.class).
                        putExtra(NAME_DATA, productsList.get(position)));
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getApplicationContext(), productsList.get(position).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}