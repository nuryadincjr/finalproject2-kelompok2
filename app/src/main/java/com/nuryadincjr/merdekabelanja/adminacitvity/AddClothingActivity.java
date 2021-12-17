package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import android.annotation.SuppressLint;
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
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddClothingActivity extends AppCompatActivity {

    private ActivityAddClothingBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private Clothing clothing;
    private boolean isEdit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddClothingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference()
                .child(CHILD_PRODUCT);
        productsPreference = ProductsPreference.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);
        SpinnersAdapter spinnersAdapter = SpinnersAdapter.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        clothing = new Clothing();
        isEdit = getIntent().getBooleanExtra(NAME_ISEDIT, false);

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(view -> imagesPreference.getMultipleImage(this));

        clothing.setCategory(getIntent().getStringExtra(NAME_PRODUCT));
        String titleBar = "Add ";
        titleBar = getIsEdited(titleBar);

        spinnersAdapter.getSpinnerAdapter(binding.actGender, R.array.gender , clothing.getGender());
        getSupportActionBar().setTitle(titleBar + clothing.getCategory());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private String getIsEdited(String titleBar) {
        if(isEdit) {
            clothing = getIntent().getParcelableExtra(NAME_DATA);
            titleBar = "Edit ";
            onDataSet(clothing);
            binding.btnAddProduct.setText("Save Product");
        }
        return titleBar;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onDataSet(Clothing clothing) {
        String  size = String.join(",", clothing.getSize());
        String  color = String.join(",", clothing.getColor());

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
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        if(isEdit) id = clothing.getId();

        String name = String.valueOf(binding.etName.getText());
        String descriptions = String.valueOf(binding.etDescriptions.getText());
        String piece = String.valueOf(binding.etPiece.getText());
        String quantity = String.valueOf(binding.etQuantity.getText());
        String brand_name  = String.valueOf(binding.etBrandName.getText());
        String sizes  = String.valueOf(binding.etSizes.getText());
        String colors  = String.valueOf(binding.etColors.getText());
        String gender  = String.valueOf(binding.actGender.getText());

        if(!name.isEmpty() && !piece.isEmpty() && !quantity.isEmpty() && !gender.isEmpty()) {
            clothing = new Clothing(id, name, descriptions, null, piece,
                    quantity, this.clothing.getCategory(),  time(), gender,
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
                                getFileExtension(uriImageList.get(i), this));
                int finalI = i;

                filePath.putFile(uriImageList.get(i)).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        photo.add(finalI, String.valueOf(task.getResult()));
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
        } else {
            assert data != null;
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    uriImageList.add(i, data.getClipData().getItemAt(i).getUri());
                }
            } else if (data.getData() != null) {
                uriImageList.add(0, data.getData());
            } else binding.btnAddPhoto.setChecked(false);
        }
    }
}