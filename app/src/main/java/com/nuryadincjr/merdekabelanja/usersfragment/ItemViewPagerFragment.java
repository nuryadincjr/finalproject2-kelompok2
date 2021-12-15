package com.nuryadincjr.merdekabelanja.usersfragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.databinding.FragmentItemViewPagerBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.usrsactivity.DetailItemProductActivity;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class ItemViewPagerFragment extends Fragment {

    private FragmentItemViewPagerBinding binding;
    public static final String ARG_CATEGORY = "category";
    public static final String ARG_TAB_INDEX = "tab index";
    private final Handler headlineHandler = new Handler();

    public ItemViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentItemViewPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null) {
            getData();
        }
    }

    private void getData() {
        Runnable headlineRunnable = () -> {
            Bundle args = getArguments();
            String category = args.getString(ARG_CATEGORY);
            int tabIndex = args.getInt(ARG_TAB_INDEX);

            String fieldName = "";
            String categoryName = "";

            switch (category) {
                case "Clothing":
                    fieldName = "gender";
                    if (tabIndex == 0) categoryName = "Male";
                    else if (tabIndex == 1) categoryName = "Female";
                    else categoryName = "General";
                    break;
                case "Electronic":
                    fieldName = "product_type";
                    if (tabIndex == 0) categoryName = "Smartphone";
                    else categoryName = "Commputer";
                    break;
                case "Book":
                    String[] categoryItem = getResources().getStringArray(R.array.book_type);
                    fieldName = "book_type";
                    categoryName = categoryItem[tabIndex];

                    break;
                case "Other Products":
                    fieldName = "category";
                    categoryName = "Other Products";
                    break;
            }

            MainViewModel mainViewModel = new MainViewModel(getActivity().getApplication());
            mainViewModel.getCategoryProductsLiveData(
                    category, fieldName, new String[]{categoryName}).observe(getViewLifecycleOwner(), products -> {

                List<Products> productsList = new ArrayList<>(products);
                ProductsAdapter productsAdapter = new ProductsAdapter(1, productsList);
                binding.rvProducts.setAdapter(productsAdapter);
                binding.rvProducts.setItemAnimator(new DefaultItemAnimator());

                onListener(productsAdapter, productsList);

                if(products.size() == 0) {
                    TextView textView = new TextView(getContext());
                    textView.setText("The product is not currently available!");
                    textView.setGravity(Gravity.CENTER);
                    binding.framelayout.addView(textView);
                    binding.rvProducts.setVisibility(View.GONE);
                }
            });
        };
        headlineHandler.post(headlineRunnable);
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
                Toast.makeText(getContext(),
                        productsList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}