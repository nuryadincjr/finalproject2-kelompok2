package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
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
import java.util.stream.Collectors;

public class AddClothingActivity extends AppCompatActivity {

    private ActivityAddClothingBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private SpinnersAdapter spinnersAdapter;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private Clothing clothing;
    private boolean isEdit;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
        isEdit = getIntent().getBooleanExtra("ISEDIT", false);

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(view -> imagesPreference.getMultipleImage(this));

        clothing.setCategory(getIntent().getStringExtra("PRODUCT"));
        String titleBar = "Add ";

        if(isEdit) {
            clothing = getIntent().getParcelableExtra("DATA");
            titleBar = "Edit ";
            onDataSet(clothing);
            binding.btnAddProduct.setText("Save Product");
        }

        spinnersAdapter.getSpinnerAdapter(binding.actGender, R.array.gender , clothing.getGender());
        getSupportActionBar().setTitle(titleBar + clothing.getCategory());
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onDataSet(Clothing clothing) {
        String  size = clothing.getSize().stream().collect(Collectors.joining(","));
        String  color = clothing.getColor().stream().collect(Collectors.joining(","));

        binding.etName.setText(clothing.getName());
        binding.etDescriptions.setText(clothing.getDescriptions());
        binding.etPiece.setText(clothing.getPiece());
        binding.etQuantity.setText(clothing.getQuantity());
        binding.etBrandName.setText(clothing.getBrand_name());
        binding.etSizes.setText(size);
        binding.etColors.setText(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        if(isEdit) id = clothing.getId();

        String name = binding.etName.getText().toString();
        String descriptions = binding.etDescriptions.getText().toString();
        String piece = binding.etPiece.getText().toString();
        String quantity = binding.etQuantity.getText().toString();
        String brand_name  = binding.etBrandName.getText().toString();
        String sizes  = binding.etSizes.getText().toString();
        String colors  = binding.etColors.getText().toString();
        String gender  = binding.actGender.getText().toString();

        if(!name.isEmpty() && !piece.isEmpty() && !quantity.isEmpty() && !gender.isEmpty()) {
            clothing = new Clothing(id, name, descriptions, null, piece,
                    quantity, this.clothing.getCategory(),  Constaint.time(), gender,
                    brand_name, imagesPreference.getList(sizes), imagesPreference.getList(colors));

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

                            if(isEdit) productsPreference.onUpdateData(clothing, dialog);
                            else productsPreference.onCreateData(clothing, this);
                        }
                    }
                });
            }
        } else {
            if(isEdit) productsPreference.onUpdateData(clothing, dialog);
            else productsPreference.onCreateData(clothing, this);
        }
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