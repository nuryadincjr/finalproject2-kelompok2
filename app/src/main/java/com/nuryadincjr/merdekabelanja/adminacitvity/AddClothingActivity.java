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
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ImageViewerAdapter;
import com.nuryadincjr.merdekabelanja.adapters.SpinnersAdapter;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddClothingBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Clothing;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.ProductsPreference;
import com.nuryadincjr.merdekabelanja.resorces.Constant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddClothingActivity extends AppCompatActivity {
    private ActivityAddClothingBinding binding;
    private StorageReference storageReference;
    private ProductsPreference productsPreference;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private Clothing clothing;
    private List<String> photo = new ArrayList<>();
    private List<String> oldPhoto;
    private boolean isEdit;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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
        titleBar = getEdited(titleBar);

        spinnersAdapter.getSpinnerAdapter(binding.actClothingType,
                R.array.clothing_type , clothing.getClothing_type());

        spinnersAdapter.getSpinnerAdapter(binding.actPeople,
                R.array.people , clothing.getPeople());

        requireNonNull(getSupportActionBar()).setTitle(titleBar + clothing.getCategory());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private String getEdited(String titleBar) {
        if(isEdit) {
            clothing = getIntent().getParcelableExtra(NAME_DATA);
            oldPhoto = new ArrayList<>(clothing.getPhoto());
            titleBar = "Edit ";
            onDataSet(clothing);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onDataSet(Clothing clothing) {
        for (String imageItem: clothing.getPhoto()) {
            uriImageList.add(Uri.parse(imageItem));
        }
        getImageViewerAdapter();


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

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        if(isEdit){
            id = clothing.getId();
            photo = clothing.getPhoto();
        }

        String name = String.valueOf(binding.etName.getText());
        String descriptions = String.valueOf(binding.etDescriptions.getText());
        String piece = String.valueOf(binding.etPiece.getText());
        String quantity = String.valueOf(binding.etQuantity.getText());
        String brandName  = String.valueOf(binding.etBrandName.getText());
        String sizes  = String.valueOf(binding.etSizes.getText());
        String colors  = String.valueOf(binding.etColors.getText());
        String people  = String.valueOf(binding.actPeople.getText());
        String clothingType  = String.valueOf(binding.actClothingType.getText());

        if(!name.isEmpty() && !piece.isEmpty() && !quantity.isEmpty() &&
                !people.isEmpty() && !clothingType.isEmpty()) {
            clothing = new Clothing(id, name, descriptions, photo, piece,
                    quantity, this.clothing.getCategory(),  time(), people,
                    brandName, clothingType, imagesPreference.getList(sizes),
                    imagesPreference.getList(colors));

            onDataCreated(clothing);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onDataCreated(Clothing clothing) {
        dialog.setMessage("Creating Data..");
        dialog.setCancelable(false);
        dialog.show();

        List<String> itemStay =  new ArrayList<>();
        List<String> itemAdded =  new ArrayList<>();
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
                        .child(clothing.getCategory())
                        .child(clothing.getId())
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
                            clothing.setPhoto(photo);
                            startResults(clothing, itemRemoved);
                        }
                    }
                });
            }
        } else startResults(clothing, itemRemoved);
    }

    private void startResults(Clothing clothing, List<String> itemRemoved) {
        if (itemRemoved.size() != 0) {
            clothing.getPhoto().removeAll(itemRemoved);
            FirebaseStorage storage = FirebaseStorage.getInstance();
            for (String itemPhoto : itemRemoved) {
                storage.getReferenceFromUrl(itemPhoto).delete();
            }
        }
        if (isEdit) productsPreference.onUpdateData(clothing, dialog);
        else productsPreference.onCreateData(clothing, this);
    }
}