package com.nuryadincjr.merdekabelanja.adminfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_FILTER_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductsAdapter;
import com.nuryadincjr.merdekabelanja.adminacitvity.AddBookActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.AddClothingActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.AddElectronicsActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.AddOthersActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsProductActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.ProductsActivity;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.FragmentProductsBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Books;
import com.nuryadincjr.merdekabelanja.models.Clothing;
import com.nuryadincjr.merdekabelanja.models.Electronics;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProductsFragment extends Fragment {
    private FragmentProductsBinding binding;
    private LocalPreference localPreference;
    private Set<String> filterProduct;
    private String[] collect;
    private Menu menu;
    private Books books;
    private Products data;
    private Clothing clothing;
    private Electronics electronics;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Products");

        collect = getResources().getStringArray(R.array.products_type);
        localPreference = LocalPreference.getInstance(getContext());
        filterProduct = localPreference.getPreferences()
                .getStringSet(KEY_FILTER_PRODUCT, new HashSet<>(Arrays.asList(collect)));

        binding.swipeRefresh.setColorSchemeResources(R.color.black);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getData();
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.rvProducts.addOnScrollListener(getScrollListener());
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ProductsActivity.class)));

        if(savedInstanceState == null) getData();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    @NonNull
    private RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !binding.fabAdd.isShown()) binding.fabAdd.show();
                else if (dy > 0 && binding.fabAdd.isShown()) binding.fabAdd.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category_product, menu);
        this.menu = menu;

        SearchView searchView = (SearchView) menu.findItem(R.id.itemSearch).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getData(s);
                return false;
            }
        });

        if(filterProduct.size() == 0 || filterProduct.size() == 4){
            isSetFilters(true,  true, false);
            filterProduct.addAll(Arrays.asList(collect));
            getData();
        } else {
            for (String filter : filterProduct) {
                if (filter.equals(collect[0])) menu.findItem(R.id.itemFilter1).setChecked(true);
                if (filter.equals(collect[1])) menu.findItem(R.id.itemFilter2).setChecked(true);
                if (filter.equals(collect[2])) menu.findItem(R.id.itemFilter3).setChecked(true);
                if (filter.equals(collect[3])) menu.findItem(R.id.itemFilter4).setChecked(true);
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemFilter0:
                        if(item.isChecked()) filterProduct.removeAll(Arrays.asList(collect));
                        else filterProduct.addAll(Arrays.asList(collect));

                        isSetFilters(!item.isChecked(), !item.isChecked(), item.isChecked());
                        break;
                    case R.id.itemFilter1:
                        if(item.isChecked()) filterProduct.remove(collect[0]);
                        else filterProduct.add(collect[0]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter2:
                        if(item.isChecked()) filterProduct.remove(collect[1]);
                        else filterProduct.add(collect[1]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter3:
                        if(item.isChecked()) filterProduct.remove(collect[2]);
                        else filterProduct.add(collect[2]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter4:
                        if(item.isChecked()) filterProduct.remove(collect[3]);
                        else filterProduct.add(collect[3]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                }
                localPreference.getEditor().putStringSet(KEY_FILTER_PRODUCT, filterProduct).apply();
                getData();
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        return super.onOptionsItemSelected(item);
    }

    private void isSetFilters(boolean bAll, boolean bChecked, boolean bEnable) {
        menu.findItem(R.id.itemFilter0).setChecked(bAll);
        menu.findItem(R.id.itemFilter1).setChecked(bChecked).setEnabled(bEnable);
        menu.findItem(R.id.itemFilter2).setChecked(bChecked).setEnabled(bEnable);
        menu.findItem(R.id.itemFilter3).setChecked(bChecked).setEnabled(bEnable);
        menu.findItem(R.id.itemFilter4).setChecked(bChecked).setEnabled(bEnable);
    }

    private void getData(String name) {
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getSearchProducts(name).observe(this, products -> {
            List<Products> productsList = new ArrayList<>(products);
            ProductsAdapter productsAdapter = new ProductsAdapter(0, productsList);

            binding.rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvProducts.setAdapter(productsAdapter);
            binding.rvProducts.setItemAnimator(new DefaultItemAnimator());

            onListener(productsAdapter, productsList);
        });
    }

    private void getData() {
        if(filterProduct.size() !=0) {
            String[] valueList = filterProduct.toArray(new String[0]);

            MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
            mainViewModel.getFilterProductsLiveData(valueList).observe(getViewLifecycleOwner(), products -> {
                List<Products> productsList = new ArrayList<>(products);
                ProductsAdapter productsAdapter = new ProductsAdapter(0, productsList);
                binding.rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rvProducts.setAdapter(productsAdapter);
                binding.rvProducts.setItemAnimator(new DefaultItemAnimator());

                onListener(productsAdapter, productsList);
            });
        }
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                ProductsFragment.this.onClick(DetailsProductActivity.class,
                        productsList.get(position), null);
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

    @SuppressLint("NonConstantResourceId")
    public void openMenuEditPopup(View view, Products products) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemCencle).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
                    getDataEdited(products);
                    break;
                case R.id.itemDelete:
                    getDataDelete(products);
                    break;
                case R.id.itemPrint:
                    onClick(AddBookActivity.class, products, NAME_ISPRINT);
                    break;
            }
            return true;
        });
        menu.show();
    }

    private void getDataEdited(Products product) {
        new ProductsRepository().getSinggleProduct(product)
                .observe(this, (Map<String, Object> maps) -> {
            ObjectMapper mapper = new ObjectMapper();

            switch (product.getCategory()){
                case "Electronic":
                    electronics = mapper.convertValue(maps, Electronics.class);
                    onClick(AddElectronicsActivity.class, electronics, NAME_ISEDIT);
                    break;
                case "Clothing":
                    clothing = mapper.convertValue(maps, Clothing.class);
                    onClick(AddClothingActivity.class, clothing, NAME_ISEDIT);
                    break;
                case "Book":
                    books = mapper.convertValue(maps, Books.class);
                    onClick(AddBookActivity.class, books, NAME_ISEDIT);
                    break;
                case "Other Products":
                    data = mapper.convertValue(maps, Products.class);
                    onClick(AddOthersActivity.class, data, NAME_ISEDIT);
                    break;
            }
        });
    }

    private <T> void onClick(Class<T> tClass, Object tData, String key) {
        startActivity(new Intent(getContext(), tClass)
                .putExtra(NAME_DATA, (Parcelable) tData)
                .putExtra(key, true));
    }
}