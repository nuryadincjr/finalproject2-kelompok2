package com.nuryadincjr.merdekabelanja.usersfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.ARG_CATEGORY;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.ARG_TAB_INDEX;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;

import android.annotation.SuppressLint;
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
import androidx.recyclerview.widget.GridLayoutManager;

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

    private final Handler headlineHandler = new Handler();

    public ItemViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemViewPagerBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(savedInstanceState == null) getData();
    }

    private void getData() {
        @SuppressLint("SetTextI18n")
        Runnable headlineRunnable = () -> {
            Bundle args = getArguments();
            assert args != null;
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
                    else categoryName = "Computer";
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

                int viewWidth = binding.rvProducts.getMeasuredWidth();
                int spanCount = (int) Math.floor(viewWidth / 360f);

                binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
                binding.rvProducts.setAdapter(productsAdapter);

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
                        putExtra(NAME_DATA, productsList.get(position)));
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(),
                        productsList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}