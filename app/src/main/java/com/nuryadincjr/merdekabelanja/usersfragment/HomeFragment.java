package com.nuryadincjr.merdekabelanja.usersfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.databinding.FragmentHomeBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.usrsactivity.CategoryActivity;
import com.nuryadincjr.merdekabelanja.usrsactivity.DetailItemProductActivity;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private MainViewModel mainViewModel;
    private String[] collect;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        collect = getResources().getStringArray(R.array.products_type);

        if(savedInstanceState == null) {
            getData();
        }

        getOnClickListener(binding.tvClothingMore, "Clothing");
        getOnClickListener(binding.tvElectronicsMore, "Electronic");
        getOnClickListener(binding.tvBooksMore, "Book");
        getOnClickListener(binding.tvOtherMore, "Other Products");

        return binding.getRoot();
    }

    private void getOnClickListener(TextView cardView, String category) {
        cardView.setOnClickListener(v -> startActivity(new Intent(getContext(),
                CategoryActivity.class).putExtra("ISCATEGORY", category)));
    }

    private void getData() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        for (int i = 0; i<collect.length; i++) {
            int finalI = i;
            mainViewModel.getFilterProductsLiveData(
            new String[]{collect[i]}).observe(getViewLifecycleOwner(), products -> {
                if(products.size() != 0) {
                    List<Products> productsList = new ArrayList<>(products);
                    ProductsAdapter productsAdapter = new ProductsAdapter(1, productsList);
                    switch (finalI){
                        case 0:
                            binding.tvClothingMore.setVisibility(View.VISIBLE);
                            binding.rvClothing.setAdapter(productsAdapter);
                            binding.rvClothing.setItemAnimator(new DefaultItemAnimator());
                            break;
                        case 1:
                            binding.tvElectronicsMore.setVisibility(View.VISIBLE);
                            binding.rvElectronics.setAdapter(productsAdapter);
                            binding.rvElectronics.setItemAnimator(new DefaultItemAnimator());
                            break;
                        case 2:
                            binding.tvBooksMore.setVisibility(View.VISIBLE);
                            binding.rvBooks.setAdapter(productsAdapter);
                            binding.rvBooks.setItemAnimator(new DefaultItemAnimator());
                            break;
                        case 3:
                            binding.tvOtherMore.setVisibility(View.VISIBLE);
                            binding.rvOther.setAdapter(productsAdapter);
                            binding.rvOther.setItemAnimator(new DefaultItemAnimator());
                            break;
                    }
                    onListener(productsAdapter, productsList);
                }
            });
        }
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(getContext(), DetailItemProductActivity.class).
                        putExtra("DATA", productsList.get(position)));
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), productsList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}