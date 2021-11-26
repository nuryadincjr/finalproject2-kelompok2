package com.nuryadincjr.merdekabelanja.adminfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetalisProductActivity;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.FragmentProductsBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ProductsFragment extends Fragment {
    private FragmentProductsBinding binding;
    private MainViewModel mainViewModel;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);

        binding.swipeRefresh.setColorSchemeResources(R.color.black);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getData();
            binding.swipeRefresh.setRefreshing(false);
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        getData();
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Products");
        super.onResume();
    }

    private void getData() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getAllProductsLiveData().observe(getViewLifecycleOwner(), products -> {
            List<Products> productsList = new ArrayList<>(products);
            ProductsAdapter productsAdapter = new ProductsAdapter(productsList);
            binding.rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvProducts.setAdapter(productsAdapter);
            binding.rvProducts.setItemAnimator(new DefaultItemAnimator());

            onListener(productsAdapter, productsList);
        });
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(getContext(), DetalisProductActivity.class)
                        .putExtra("DATA", productsList.get(position))
                        .putExtra("ISDETAIL", "ADMIN"));
            }

            @Override
            public void onLongClick(View view, int position) {
                openMenuEditPopup(view, productsList.get(position));
            }
        });
    }

    private void getDataDelete(Products products) {
        new ProductsRepository().deleteProduct(products.getId()).addOnSuccessListener(unused -> {
            if (products.getPhoto() != null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                List<String> listPhotos = products.getPhoto();
                for(String itemPhoto: listPhotos) {
                    storage.getReferenceFromUrl(itemPhoto).delete();
                }
            }
        });
        getData();
    }

    public void openMenuEditPopup(View view, Products products) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemCencle).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
//                    startActivity(new Intent(getContext(), EditProductActivity.class)
//                            .putExtra("DATA", products)
//                            .putExtra("ISEDIT", true));
                    break;
                case R.id.itemDelete:
                    getDataDelete(products);
                    break;
                case R.id.itemPrint:
                    PdfConverters.getInstance(getContext())
                            .getDataToPdf(binding.getRoot(), products.getId());
                    break;
            }
            return true;
        });
        menu.show();
    }
}