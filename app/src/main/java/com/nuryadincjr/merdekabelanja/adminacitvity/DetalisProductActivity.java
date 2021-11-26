package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ItemLableAdapter;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetalisProductBinding;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.List;
import java.util.Map;

public class DetalisProductActivity extends AppCompatActivity {

    private ActivityDetalisProductBinding binding;
    private FirebaseStorage storage;
    private Products data;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalis_product);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityDetalisProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        data = getIntent().getParcelableExtra("DATA");

        onDataSet(data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        this.menu = menu;

        setVisibleMenu(false, true);
        return true;
    }

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
                getDataDelete();
                return true;
            case R.id.itemPrint:
                PdfConverters.getInstance(this)
                        .getDataToPdf(binding.getRoot(), data.getId());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataEdited() {
//       startActivity(new Intent(getContext(), EditProductActivity.class)
//               .putExtra("DATA", products)
//               .putExtra("ISEDIT", true));
    }

    private void getDataDelete() {
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

    private void setVisibleMenu(boolean visible1, boolean visible2) {
        menu.findItem(R.id.itemSaves).setVisible(visible1);
        menu.findItem(R.id.itemCencle).setVisible(visible1);
        menu.findItem(R.id.itemEdit).setVisible(visible2);
        menu.findItem(R.id.itemDelete).setVisible(visible2);
        menu.findItem(R.id.itemPrint).setVisible(visible2);
    }

    private void onDataSet(Products product) {
        new ProductsRepository().getSinggleProduct(product).observe(this, maps -> {
            Object[] key = maps.keySet().toArray();
            Map<String, Object> value = maps;

            ItemLableAdapter itemLableAdapter = new ItemLableAdapter(key, value, binding);
            binding.rvLable.setLayoutManager(new LinearLayoutManager(this));
            binding.rvLable.setAdapter(itemLableAdapter);
            binding.rvLable.setItemAnimator(new DefaultItemAnimator());
        });
    }
}