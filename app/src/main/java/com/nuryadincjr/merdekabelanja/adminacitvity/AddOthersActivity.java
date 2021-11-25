package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddOthersBinding;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddOthersActivity extends AppCompatActivity {

    private ActivityAddOthersBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private List<Uri> uriImageList;
    private ProgressDialog dialog;
    private Products products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_others);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddOthersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");
        productsPreference = ProductsPreference.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        products = new Products();

        products.setCategory(getIntent().getStringExtra("PRODUCT"));
        getSupportActionBar().setTitle("Add " + products.getCategory());

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getMultipleImage(this));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        String name = binding.etName.getText().toString();
        String descriptions = binding.etDescriptions.getText().toString();
        String piece = binding.etPiece.getText().toString();
        String quantity = binding.etQuantity.getText().toString();

        if(!name.isEmpty() && !piece.isEmpty() && !quantity.isEmpty()) {
            products = new Products(id, name, descriptions, null,
                    piece, quantity, this.products.getCategory());
            onCreateProduct(products);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onCreateProduct(Products products) {
        dialog.setMessage("Createing Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> photo = new ArrayList<>();
        products.setPhoto(photo);

        if (!uriImageList.isEmpty()) {
            dialog.setMessage("Setuping data..");

            for (int i = 0; i < uriImageList.size(); i++) {
                StorageReference filePath = storageReference
                        .child(products.getCategory())
                        .child(products.getId())
                        .child("preview" + i + "." + Constaint.getFileExtension(uriImageList.get(i), this));
                int finalI = i;

                filePath.putFile(uriImageList.get(i)).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        photo.add(finalI, task.getResult().toString());
                        if ((finalI + 1) == uriImageList.size()) {
                            products.setPhoto(photo);
                            productsPreference.onCreateData(products, this);
                        }
                    }
                });
            }
        } else productsPreference.onCreateData(products, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 25 || resultCode != -1) {
            this.binding.btnAddPhoto.setChecked(false);
        } else if (data.getClipData() != null) {
            int count = data.getClipData().getItemCount();
            for (int i = 0; i < count; i++) {
                uriImageList.add(i, data.getClipData().getItemAt(i).getUri());
            }
        } else if (data.getData() != null) {
            uriImageList.add(0, data.getData());
        } else binding.btnAddPhoto.setChecked(false);
    }
}