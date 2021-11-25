package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.widget.AdapterView.OnItemSelectedListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.SpinnersAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddElectronicsBinding;
import com.nuryadincjr.merdekabelanja.models.Electronics;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddElectronicsActivity extends AppCompatActivity implements OnItemSelectedListener {

    private ActivityAddElectronicsBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private SpinnersAdapter spinnersAdapter;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private Electronics electronics;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_electronics);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddElectronicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");
        productsPreference = ProductsPreference.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);
        spinnersAdapter = SpinnersAdapter.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        electronics = new Electronics();

        electronics.setCategory(getIntent().getStringExtra("PRODUCT"));
        getSupportActionBar().setTitle("Add " + electronics.getCategory());

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(view -> imagesPreference.getMultipleImage(this));

        spinnersAdapter.getSpinnerAdapter(binding.spElectronicType, R.array.electronic_type);
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
        String brand_name  = binding.etBrandName.getText().toString();

        if(!name.isEmpty() && !piece.isEmpty() && !quantity.isEmpty() &&
                !electronics.getProduct_type().equals("Select Electronic Type")) {

            Electronics data = new Electronics(id, name, descriptions, null, piece, quantity,
                    electronics.getCategory(), brand_name, electronics.getProduct_type());

            onCreateProduct(data);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onCreateProduct(Electronics electronics) {
        dialog.setMessage("Createing Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> photo = new ArrayList<>();
        electronics.setPhoto(photo);

        if(!uriImageList.isEmpty()){
            dialog.setMessage("Uploading file..");

            for (int i = 0; i < uriImageList.size(); i++) {
                StorageReference filePath = storageReference
                        .child(electronics.getCategory())
                        .child(electronics.getId())
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
                            electronics.setPhoto(photo);
                            productsPreference.onCreateData(electronics, this);
                        }
                    }
                });
            }
        } else productsPreference.onCreateData(electronics, this);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position ==0) view.setEnabled(false);
        electronics.setProduct_type(parent.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        electronics.setProduct_type(parent.getSelectedItem().toString());
    }
}