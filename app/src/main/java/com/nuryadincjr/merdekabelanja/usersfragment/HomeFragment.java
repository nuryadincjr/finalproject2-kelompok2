package com.nuryadincjr.merdekabelanja.usersfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.*;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.google.android.material.tabs.TabLayoutMediator;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.HeadlineAdapter;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.databinding.FragmentHomeBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Headline;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.resorces.Constant;
import com.nuryadincjr.merdekabelanja.resorces.Headlines;
import com.nuryadincjr.merdekabelanja.usrsactivity.CategoryActivity;
import com.nuryadincjr.merdekabelanja.usrsactivity.DetailItemProductActivity;
import com.nuryadincjr.merdekabelanja.usrsactivity.SearchActivity;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private MainViewModel mainViewModel;
    private String[] collect;

    private final Handler headlineHandler = new Handler();
    private final Runnable headlineRunnable = new Runnable() {
        @Override
        public void run() {
            if(binding.vpHeadline.getCurrentItem() >=2) {
                binding.vpHeadline.setCurrentItem(0, true);
            } else {
                binding.vpHeadline.setCurrentItem(
                        binding.vpHeadline.getCurrentItem() + 1, true);
            }
        }
    };

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        collect = getResources().getStringArray(R.array.products_type);

        getOnClickListener(binding.tvClothingMore, "Clothing");
        getOnClickListener(binding.tvElectronicsMore, "Electronic");
        getOnClickListener(binding.tvBooksMore, "Book");
        getOnClickListener(binding.tvOtherMore, "Other Products");

        binding.searchBar.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                startActivity(new Intent(getContext(), SearchActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        getData();
        onHeadlineAdapter();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        new TabLayoutMediator(binding.tabLayout, binding.vpHeadline,
                (tab, position) -> tab.setText("Item " + (position + 1))
        ).attach();
        super.onViewCreated(view, savedInstanceState);
    }

    private void onHeadlineAdapter() {
        Headline[] headlines = Headlines.getHeadlines();
        List<Headline> headlineList = new ArrayList<>();
        int i = 0;
        for(Headline headline: headlines){
            headlineList.add(i, headline);
        }

        HeadlineAdapter headlineAdapter = new HeadlineAdapter(headlineList, binding.vpHeadline);
        binding.vpHeadline.setAdapter(headlineAdapter);
        binding.vpHeadline.setOffscreenPageLimit(3);
        binding.vpHeadline.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        binding.tabLayout.setSelected(true);
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        transformer.addTransformer((page, position) -> {
            float transport = 1 - Math.abs(position);
            page.setScaleY(0.85F + transport * 0.15F);
        });

        binding.vpHeadline.setPageTransformer(transformer);
        binding.vpHeadline.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                headlineHandler.removeCallbacks(headlineRunnable);
                headlineHandler.postDelayed(headlineRunnable, 3000);
            }
        });

        headlineAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                String category = "";
                switch (position){
                    case 0:
                        category ="Clothing";
                        break;
                    case 1:
                        category = "Electronic";
                        break;
                    case 2:
                        category ="Book";
                        break;
                }
                HomeFragment.this.onClick(
                        category);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.searchBar.removeCallbacks(headlineRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.searchBar.clearFocus();
        binding.vpHeadline.postDelayed(headlineRunnable, 3000);
    }

    private void getOnClickListener(TextView cardView, String category) {
        cardView.setOnClickListener(v -> onClick(
                category));
    }

    private void getData() {
        Runnable headlineRunnable = () -> {
            mainViewModel = new MainViewModel(requireActivity().getApplication());
            for (int i = 0; i<collect.length; i++) {
                int finalI = i;
                mainViewModel.getFilterProductsLiveData(new String[]{collect[i]})
                        .observe(getViewLifecycleOwner(), products -> onDataSet(finalI, products));
            }
        };
        headlineHandler.post(headlineRunnable);
    }

    private void onDataSet(int finalI, ArrayList<Products> products) {
        if(products.size() != 0) {
            List<Products> productsList = new ArrayList<>(products);
            ProductsAdapter productsAdapter = new ProductsAdapter(1, productsList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
            layoutManager.setOrientation(RecyclerView.HORIZONTAL);

            switch (finalI){
                case 0:
                    onRecyclerView(productsAdapter, layoutManager, binding.rvClothing);
                    break;
                case 1:
                    onRecyclerView(productsAdapter, layoutManager, binding.rvElectronics);
                    break;
                case 2:
                    onRecyclerView(productsAdapter, layoutManager, binding.rvBooks);
                    break;
                case 3:
                    onRecyclerView(productsAdapter, layoutManager, binding.rvOther);
                    break;
            }
            onListener(productsAdapter, productsList);

        }else {
            switch (finalI){
                case 0:
                    isEmptyMessage(binding.llClothing, binding.rvClothing);
                    break;
                case 1:
                    isEmptyMessage(binding.llElectronic, binding.rvElectronics);
                    break;
                case 2:
                    isEmptyMessage(binding.llBooks, binding.rvBooks);
                    break;
                case 3:
                    isEmptyMessage(binding.llOther, binding.rvOther);
                    break;
            }
        }
    }

    private void onRecyclerView(ProductsAdapter productsAdapter,
                                LinearLayoutManager layoutManager, ShimmerRecyclerView asRecyclerView) {
        asRecyclerView.setLayoutManager(layoutManager);
        asRecyclerView.setAdapter(productsAdapter);
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(getContext(), DetailItemProductActivity.class)
                        .putExtra(NAME_DATA, productsList.get(position)));
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), productsList.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onClick(String value) {
        startActivity(new Intent(getContext(), CategoryActivity.class)
                .putExtra(NAME_CATEGORY, value));
    }
}