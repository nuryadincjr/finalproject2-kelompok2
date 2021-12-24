package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ImageViewerAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddOthersBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;
import com.nuryadincjr.merdekabelanja.resorces.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddOthersActivity extends AppCompatActivity {
    private ActivityAddOthersBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private Products products;
    private List<Uri> uriImageList;
    private List<String> photo;
    private List<String> oldPhoto;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_others);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddOthersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference()
                .child(CHILD_PRODUCT);
        productsPreference = ProductsPreference.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        oldPhoto = new ArrayList<>();
        photo = new ArrayList<>();
        products = new Products();
        isEdit = getIntent().getBooleanExtra(NAME_ISEDIT, false);

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getMultipleImage(this));

        products.setCategory(getIntent().getStringExtra(NAME_PRODUCT));
        String titleBar = "Add ";
        titleBar = getEdited(titleBar);

        requireNonNull(getSupportActionBar()).setTitle(titleBar + products.getCategory());
    }

    @SuppressLint("SetTextI18n")
    private String getEdited(String titleBar) {
        if(isEdit) {
            products = getIntent().getParcelableExtra(NAME_DATA);
            oldPhoto = new ArrayList<>(products.getPhoto());
            titleBar = "Edit ";
            onDataSet(products);
            binding.btnAddProduct.setText("Save Product");
        }
        return titleBar;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 && data != null) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                int dataList = uriImageList.size() + count;

                if(dataList <= 3){
                    for (int i = 0; i < count; i++) {
                        uriImageList.add(data.getClipData().getItemAt(i).getUri());
                    }
                }else{
                    Toast.makeText(this, "The quota for adding images is "+(3-uriImageList.size()),
                            Toast.LENGTH_SHORT).show();
                    imagesPreference.getMultipleImage(this);
                }
            } else uriImageList.add(data.getData());
        }

        getImageViewerAdapter();
    }

    private void getImageViewerAdapter() {
        ImageViewerAdapter imageViewerAdapter = new ImageViewerAdapter(uriImageList);
        binding.rvImageViewer.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false));
        binding.rvImageViewer.setAdapter(imageViewerAdapter);

        onClickListener(imageViewerAdapter);
    }

    private void onClickListener(ImageViewerAdapter imageViewerAdapter) {
        imageViewerAdapter.setItemClickListener(new ItemClickListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onClick(View view, int position) {
                uriImageList.remove(position);
                if(photo.size()!=0) photo.remove(position);
                imageViewerAdapter.notifyDataSetChanged();
                binding.btnAddPhoto.setEnabled(uriImageList.size() < 3);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        binding.btnAddPhoto.setEnabled(uriImageList.size() < 3);
    }

    private void onDataSet(Products products) {
        for (String imageItem: products.getPhoto()) {
            uriImageList.add(Uri.parse(imageItem));
        }
        getImageViewerAdapter();

        binding.etName.setText(products.getName());
        binding.etDescriptions.setText(products.getDescriptions());
        binding.etPiece.setText(products.getPiece());
        binding.etQuantity.setText(products.getQuantity());
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        if(isEdit){
            id = products.getId();
            photo = products.getPhoto();
        }

        String name = String.valueOf(binding.etName.getText());
        String descriptions = String.valueOf(binding.etDescriptions.getText());
        String piece = String.valueOf(binding.etPiece.getText());
        String quantity = String.valueOf(binding.etQuantity.getText());

        if(!name.isEmpty() && !piece.isEmpty() && !quantity.isEmpty()) {
            products = new Products(id, name, descriptions, photo,
                    piece, quantity, this.products.getCategory(), time());
            onDataCreated(products);
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onDataCreated(Products products) {
        dialog.setMessage("Creating Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> itemAdded =  new ArrayList<>();
        List<String> itemStay =  new ArrayList<>();
        List<String> itemRemoved =  new ArrayList<>(oldPhoto);

        for (Uri item: uriImageList) {
            Pattern p = Pattern.compile(Constant.PATTERN_LABEL);
            Matcher m = p.matcher(String.valueOf(item));
            if(m.find()) {
                itemStay.add(String.valueOf(item));
            } else itemAdded.add(String.valueOf(item));
        }

        itemRemoved.removeAll(itemStay);

        if (!itemAdded.isEmpty()) {
            dialog.setMessage("Uploading file..");

            for (int i = 0; i < itemAdded.size(); i++) {
                StorageReference filePath = storageReference
                        .child(products.getCategory())
                        .child(products.getId())
                        .child("preview" + i + "." + getFileExtension(Uri.parse(itemAdded.get(i)), this));
                int finalI = i;

                filePath.putFile(Uri.parse(itemAdded.get(i))).continueWithTask(task -> {
                    if (!task.isSuccessful()) {
                        dialog.dismiss();
                        throw requireNonNull(task.getException());
                    }
                    return filePath.getDownloadUrl();
                }).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        photo.add(String.valueOf(task.getResult()));
                        if ((finalI + 1) == itemAdded.size()) {
                            products.setPhoto(photo);
                            startResults(products, itemRemoved);
                        }
                    }
                });
            }
        } else startResults(products, itemRemoved);
    }

    private void startResults(Products products, List<String> itemRemoved) {
        if (itemRemoved.size() != 0) {
            products.getPhoto().removeAll(itemRemoved);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            for (String itemPhoto : itemRemoved) {
                storage.getReferenceFromUrl(itemPhoto).delete();
            }
        }
        if (isEdit) productsPreference.onUpdateData(products, dialog);
        else productsPreference.onCreateData(products, this);
    }
}