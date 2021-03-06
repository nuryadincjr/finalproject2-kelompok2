package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.pojo.PermissionsAccess.requestStoragePermission;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;
import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductItemAdapter;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetalisProductBinding;
import com.nuryadincjr.merdekabelanja.models.Books;
import com.nuryadincjr.merdekabelanja.models.Clothing;
import com.nuryadincjr.merdekabelanja.models.Electronics;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.List;
import java.util.Map;

public class DetailsProductActivity extends AppCompatActivity {
    private ActivityDetalisProductBinding binding;
    private FirebaseStorage storage;
    private Books books;
    private Products data;
    private Clothing clothing;
    private Electronics electronics;
    private boolean isPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis_product);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details Product");

        binding = ActivityDetalisProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        data = getIntent().getParcelableExtra(NAME_DATA);
        isPrint = getIntent().getBooleanExtra(NAME_ISPRINT, false);
    }

    @Override
    protected void onResume() {
        getData(data);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        menu.findItem(R.id.itemSaves).setVisible(false);
        menu.findItem(R.id.itemClose).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemEdit:
                getDataEdited();
                return true;
            case R.id.itemDelete:
                getDataDeleted();
                return true;
            case R.id.itemPrint:
                getDataPrinted();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(Products product) {
        new ProductsRepository().getSinggleProduct(product)
                .observe(this, maps -> onDataSet(maps, product));
    }

    private void getDataEdited() {
        switch (data.getCategory()){
            case "Electronic":
                onClick(AddElectronicsActivity.class, electronics);
                break;
            case "Clothing":
                onClick(AddClothingActivity.class, clothing);
                break;
            case "Book":
                onClick(AddBookActivity.class, books);
                break;
            case "Other Products":
                onClick(AddOthersActivity.class, data);
                break;
        }
    }
    private void getDataDeleted() {
        new ProductsRepository().deleteProduct(data.getId()).addOnSuccessListener(unused -> {
            if (data.getPhoto() != null){
                List<String> listPhotos = data.getPhoto();
                for(String itemPhoto: listPhotos) {
                    storage.getReferenceFromUrl(itemPhoto).delete();
                }
            }
        });
        finish();
    }

    private void getDataPrinted() {
        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            Snackbar.make(binding.getRoot(),
                    R.string.storage_permission_available,
                    Snackbar.LENGTH_SHORT).show();
            startPrinting();
        } else {
            requestStoragePermission(this, binding.getRoot());
        }
    }

    private void startPrinting() {
        if(binding.getRoot().getWidth() != 0 &&
                binding.getRoot().getHeight() !=0){
            PdfConverters.getInstance(this)
                    .getDataToPdf(binding.getRoot(), data.getId());
        }
    }

    private void onDataSet(Map<String, Object> maps, Products product) {
        Object[] key = maps.keySet().toArray();

        ObjectMapper mapper = new ObjectMapper();
        switch (product.getCategory()) {
            case "Electronic":
                electronics = mapper.convertValue(maps, Electronics.class);
                break;
            case "Clothing":
                clothing = mapper.convertValue(maps, Clothing.class);
                break;
            case "Book":
                books = mapper.convertValue(maps, Books.class);
                break;
            case "Other Products":
                data = mapper.convertValue(maps, Products.class);
                break;
        }

        ProductItemAdapter productItemAdapter = new ProductItemAdapter(key, maps, 0, binding);
        binding.rvLable.setLayoutManager(new LinearLayoutManager(DetailsProductActivity.this));
        binding.rvLable.setAdapter(productItemAdapter);
        binding.rvLable.setItemAnimator(new DefaultItemAnimator());

        if (isPrint) DetailsProductActivity.this.getDataPrinted();
    }

    private <T> void onClick(Class<T> tClass, Object tData) {
        startActivity(new Intent(this, tClass)
                .putExtra(NAME_DATA, (Parcelable) tData)
                .putExtra(NAME_ISEDIT, true));
    }
}