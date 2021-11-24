package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddClothingBinding;
import com.nuryadincjr.merdekabelanja.models.Clothing;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddClothingActivity extends AppCompatActivity implements OnItemSelectedListener {

    private ActivityAddClothingBinding binding;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private final String TAG = "LIA";
    private Clothing clothing;
    private int positionOfSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddClothingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        clothing = new Clothing();

        clothing.setCategory(getIntent().getStringExtra("PRODUCT"));
        getSupportActionBar().setTitle("Add " + clothing.getCategory());

        binding.btnAddProduct.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 25);
        });

        getGenderAdapter();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getGenderAdapter() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spGender.setOnItemSelectedListener(this);
        binding.spGender.setAdapter(adapter);
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
                !quantity.isEmpty() && clothing.getGender() != null) {

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
                            clothing.setPhoto(photo);
                            onCreateData(clothing);
                        }
                    }
                });
            }
        } else onCreateData(clothing);
    }

    private void onCreateData(Clothing clothing) {
        dialog.setMessage("Setuping data..");

        new ProductsRepository().insertProducts(clothing).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
            finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(),
                    "Error adding document.", Toast.LENGTH_SHORT).show();
        });
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
        if(position !=0){
            clothing.setGender(parent.getSelectedItem().toString());
        }else {
            view.setEnabled(false);
            clothing.setGender(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}