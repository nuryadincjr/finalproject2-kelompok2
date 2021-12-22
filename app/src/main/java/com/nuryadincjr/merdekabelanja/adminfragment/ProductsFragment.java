package com.nuryadincjr.merdekabelanja.adminfragment;

import static com.nuryadincjr.merdekabelanja.pojo.ImagesPreference.getStringReplace;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_CATEGORY_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getStartActivity;
import static java.util.Arrays.*;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.nuryadincjr.merdekabelanja.resorces.Categoryes;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ProductsFragment extends Fragment {
    private FragmentProductsBinding binding;
    private LocalPreference localPreference;
    private List<String> productType;
    private Menu menu;
    private Books books;
    private Products data;
    private Clothing clothing;
    private Electronics electronics;
    private Categoryes categoryes;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProductsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Products");

        categoryes = Categoryes.getInstance(getContext());
        localPreference = new LocalPreference(requireContext());

        String productsTypePreference = localPreference.getPreferences()
                .getString(KEY_CATEGORY_PRODUCT, getStringReplace(Arrays.toString(categoryes.productsType())));

        List<String> collections = asList(productsTypePreference.split(", ").clone());
        productType = new ArrayList<>(collections);
        productType.remove("");

        binding.swipeRefresh.setOnRefreshListener(this::onRefresh);
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

        if(productType.size() == 0 || productType.size() == 4){
            isSetFilters(true,  true, false);
        } else {
            for (String filter : productType) {
                if (filter.equals(categoryes.productsType()[0])) menu.findItem(R.id.itemFilter1).setChecked(true);
                if (filter.equals(categoryes.productsType()[1])) menu.findItem(R.id.itemFilter2).setChecked(true);
                if (filter.equals(categoryes.productsType()[2])) menu.findItem(R.id.itemFilter3).setChecked(true);
                if (filter.equals(categoryes.productsType()[3])) menu.findItem(R.id.itemFilter4).setChecked(true);
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
                        isSetFilters(!item.isChecked(), !item.isChecked(), item.isChecked());
                        productType.clear();

                        localPreference.getEditor()
                                .putString(KEY_CATEGORY_PRODUCT, getStringReplace(productType))
                                .apply();
                        getData();
                        break;
                    case R.id.itemFilter1:
                        getFilterChecked(item, 0);
                        break;
                    case R.id.itemFilter2:
                        getFilterChecked(item, 1);
                        break;
                    case R.id.itemFilter3:
                        getFilterChecked(item, 2);
                        break;
                    case R.id.itemFilter4:
                        getFilterChecked(item, 3);
                        break;
                }
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        return super.onOptionsItemSelected(item);
    }

    private void getFilterChecked(MenuItem item, int i) {
        if (item.isChecked()) productType.remove(categoryes.productsType()[i]);
        else productType.add(categoryes.productsType()[i]);

        menu.findItem(R.id.itemFilter0).setChecked(false);
        item.setChecked(!item.isChecked());

        localPreference.getEditor()
                .putString(KEY_CATEGORY_PRODUCT, getStringReplace(productType))
                .apply();
        getData();
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
        mainViewModel.getSearchProducts(name).observe(this, this::onDataSet);
    }

    private void getData() {
        String[] valueList = productType.toArray(new String[0]);
        if(productType.size() == 0) valueList = categoryes.productsType();

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getFilterProductsLiveData(valueList).observe(getViewLifecycleOwner(), this::onDataSet);
    }


    private void getDataDeleted(Products products) {
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

    private void getDataEdited(Products product) {
        new ProductsRepository().getSinggleProduct(product)
                .observe(this, (Map<String, Object> maps) -> {
                    ObjectMapper mapper = new ObjectMapper();

                    Log.d(TAG, "getDataEdited: map " + maps);
                    switch (product.getCategory()){
                        case "Electronic":
                            electronics = mapper.convertValue(maps, Electronics.class);
                            getStartActivity(requireContext(),
                                    AddElectronicsActivity.class, electronics, NAME_ISEDIT);
                            break;
                        case "Clothing":
                            clothing = mapper.convertValue(maps, Clothing.class);
                            getStartActivity(requireContext(),
                                    AddClothingActivity.class, clothing, NAME_ISEDIT);
                            break;
                        case "Book":
                            books = mapper.convertValue(maps, Books.class);
                            getStartActivity(requireContext(),
                                    AddBookActivity.class, books, NAME_ISEDIT);
                            break;
                        case "Other Products":
                            data = mapper.convertValue(maps, Products.class);
                            getStartActivity(requireContext(),
                                    AddOthersActivity.class, data, NAME_ISEDIT);
                            break;
                    }
                });
    }

    private void onDataSet(ArrayList<Products> products) {
        List<Products> productsList = new ArrayList<>(products);
        ProductsAdapter productsAdapter = new ProductsAdapter(0, productsList);

        binding.rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvProducts.setAdapter(productsAdapter);
        binding.rvProducts.setItemAnimator(new DefaultItemAnimator());

        onListener(productsAdapter, productsList);
    }

    private void onListener(ProductsAdapter productsAdapter, List<Products> productsList) {
        productsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                getStartActivity(requireContext(),
                        DetailsProductActivity.class, productsList.get(position), null);
            }

            @Override
            public void onLongClick(View view, int position) {
                openMenuEditPopup(view, productsList.get(position));
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void openMenuEditPopup(View view, Products products) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemClose).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
                    getDataEdited(products);
                    break;
                case R.id.itemDelete:
                    getDataDeleted(products);
                    break;
                case R.id.itemPrint:
                    getStartActivity(requireContext(),
                            DetailsProductActivity.class, products, NAME_ISPRINT);
                    break;
            }
            return true;
        });
        menu.show();
    }

    private void onRefresh() {
        binding.swipeRefresh.setRefreshing(false);
        getData();
    }
}