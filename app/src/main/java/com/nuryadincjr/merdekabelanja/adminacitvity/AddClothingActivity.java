package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.SpinnersAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddClothingBinding;
import com.nuryadincjr.merdekabelanja.models.Clothing;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddClothingActivity extends AppCompatActivity implements OnItemSelectedListener {

    private ActivityAddClothingBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private SpinnersAdapter spinnersAdapter;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private Clothing clothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddClothingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");
        productsPreference = ProductsPreference.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);
        spinnersAdapter = SpinnersAdapter.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        clothing = new Clothing();

        clothing.setCategory(getIntent().getStringExtra("PRODUCT"));
        getSupportActionBar().setTitle("Add " + clothing.getCategory());

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(view -> imagesPreference.getMultipleImage(this));

        spinnersAdapter.getSpinnerAdapter(binding.spGender, R.array.gender);
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
        String sizes  = binding.etSizes.getText().toString();
        String colors  = binding.etColors.getText().toString();

        if(!name.isEmpty() && !piece.isEmpty() &&
                !quantity.isEmpty() && !clothing.getGender().equals("Select Gender")) {

            clothing = new Clothing(id, name, descriptions, null, piece,
                    quantity, this.clothing.getCategory(), this.clothing.getGender(),
                    brand_name, getList(sizes), getList(colors));

            onCreateProduct(clothing);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onCreateProduct(Clothing clothing) {
        dialog.setMessage("Createing Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> photo = new ArrayList<>();
        clothing.setPhoto(photo);

        if(!uriImageList.isEmpty()){
            dialog.setMessage("Uploading file..");

            for (int i = 0; i < uriImageList.size(); i++) {
                StorageReference filePath = storageReference
                        .child(clothing.getCategory())
                        .child(clothing.getId())
                        .child("preview" + i + "." +
                                Constaint.getFileExtension(uriImageList.get(i), this));
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
                            clothing.setPhoto(photo);
                            productsPreference.onCreateData(clothing, this);
                        }
                    }
                });
            }
        } else productsPreference.onCreateData(clothing, this);
    }

    private List<String> getList(String str) {
        String[] myStrings = str.split(",");
        List<String> stringList = new ArrayList<>();
        for (int i = 0; i < myStrings.length; i++) {
            stringList.add(i, myStrings[i]);
        }
        return stringList;
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
        clothing.setGender(parent.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        clothing.setGender(parent.getSelectedItem().toString());
    }
}