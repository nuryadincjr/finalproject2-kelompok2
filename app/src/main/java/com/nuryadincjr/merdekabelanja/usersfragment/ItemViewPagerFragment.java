package com.nuryadincjr.merdekabelanja.usersfragment;

import static com.nuryadincjr.merdekabelanja.pojo.ImagesPreference.getStringReplace;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.ARG_CATEGORY;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.ARG_TAB_INDEX;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_CATEGORY_CLOTHING;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static java.util.Arrays.asList;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
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
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.resorces.Categoryes;
import com.nuryadincjr.merdekabelanja.usrsactivity.DetailItemProductActivity;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemViewPagerFragment extends Fragment {
    private FragmentItemViewPagerBinding binding;
    private LocalPreference localPreference;
    private final Handler headlineHandler = new Handler();
    private Categoryes categoryes;
    private String category;
    private int position;
    private SubMenu subMenu;
    private List<String> clothingType;

    public ItemViewPagerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentItemViewPagerBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        assert args != null;
        category = args.getString(ARG_CATEGORY);
        position = args.getInt(ARG_TAB_INDEX);

        categoryes = Categoryes.getInstance(getContext());
        localPreference = LocalPreference.getInstance(getContext());

        String productsTypePreference = localPreference.getPreferences()
                .getString(KEY_CATEGORY_CLOTHING, getStringReplace(Arrays.toString(categoryes.clothingType())));
        List<String> collections = asList(productsTypePreference.split(", ").clone());

        clothingType = new ArrayList<>(collections);
        clothingType.remove("");

        if(savedInstanceState == null) getData();
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if(category.equals("Clothing")){
            inflater.inflate(R.menu.menu_category_clothing, menu);

            SubMenu subMenu = menu.getItem(0).getSubMenu();
            this.subMenu = subMenu;

            if(clothingType.size() ==4 || clothingType.size() ==0){
                menu.findItem(R.id.itemFilter0).setChecked(true);
                for(int i = 0; i < categoryes.clothingType().length; i++){
                    subMenu.add(0, i, 0, categoryes.clothingType()[i]);
                    subMenu.setGroupCheckable(0, true, true);
                }
            } else {
                for (String filter : clothingType) {
                    for(int i = 0; i < categoryes.clothingType().length; i++){
                        if(filter.equals(categoryes.clothingType()[i])){
                            subMenu.add(0, i, 0, categoryes.clothingType()[i]).setChecked(true);
                        }else subMenu.add(0, i, 0, categoryes.clothingType()[i]);
                        subMenu.setGroupCheckable(0, true, true);
                    }
                }
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.itemFilter0) {
            item.setChecked(!item.isChecked());
            subMenu.setGroupCheckable(0, item.isChecked(), true);
            clothingType.clear();
            localPreference.getEditor()
                    .putString(KEY_CATEGORY_CLOTHING, getStringReplace(clothingType))
                    .apply();
            getData();
        }

        for (int i = 0; i< categoryes.clothingType().length; i++){
            if (item.getItemId() == i){
                localPreference.getEditor()
                        .putString(KEY_CATEGORY_CLOTHING, categoryes.clothingType()[item.getItemId()])
                        .apply();
                item.setChecked(!item.isChecked());
                getData();
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void getData() {
        @SuppressLint("SetTextI18n")
        Runnable headlineRunnable = () -> {
            String fieldName = "";
            String categoryName = "";

            switch (category) {
                case "Clothing":
                    fieldName = "people";
                    categoryName = categoryes.people()[position];
                    break;
                case "Electronic":
                    fieldName = "electronic_type";
                    categoryName = categoryes.electronicType()[position];
                    break;
                case "Book":
                    fieldName = "book_type";
                    categoryName = categoryes.bookType()[position];
                    break;
                case "Other Products":
                    fieldName = "category";
                    categoryName = "Other Products";
                    break;
            }

            if(category.equals("Clothing")) {
                onGetData(categoryName);
            } else onGetData(fieldName, categoryName);

        };
        headlineHandler.post(headlineRunnable);
    }

    private void onGetData(String people ) {
        String productsTypePreference = localPreference.getPreferences()
                .getString(KEY_CATEGORY_CLOTHING, getStringReplace(Arrays.toString(categoryes.clothingType())));
        List<String> collections = asList(productsTypePreference.split(", ").clone());

        clothingType = new ArrayList<>(collections);
        clothingType.remove("");

        String[] valueList = clothingType.toArray(new String[0]);
        if(clothingType.size() == 0) valueList = categoryes.clothingType();

        MainViewModel mainViewModel = new MainViewModel(requireActivity().getApplication());
        mainViewModel.getCategoryClothingLiveData(
                category, people, "clothing_type", valueList)
                .observe(getViewLifecycleOwner(), this::onDataSet);
    }

    private void onGetData(String fieldName, String categoryName) {
        MainViewModel mainViewModel = new MainViewModel(requireActivity().getApplication());
        mainViewModel.getCategoryProductsLiveData(
                category, fieldName, new String[]{categoryName})
                .observe(getViewLifecycleOwner(), this::onDataSet);
    }

    private void onDataSet(ArrayList<Products> products) {
        List<Products> productsList = new ArrayList<>(products);
        ProductsAdapter productsAdapter = new ProductsAdapter(1, productsList);

        int viewWidth = binding.rvProducts.getMeasuredWidth();
        int spanCount = (int) Math.floor(viewWidth / 360f);

        binding.rvProducts.setLayoutManager(new GridLayoutManager(getContext(), spanCount));
        binding.rvProducts.setAdapter(productsAdapter);
        onListener(productsAdapter, productsList);
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