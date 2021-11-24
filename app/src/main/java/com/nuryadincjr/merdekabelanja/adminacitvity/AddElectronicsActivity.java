package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.widget.AdapterView.OnItemSelectedListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddElectronicsBinding;
import com.nuryadincjr.merdekabelanja.models.Electronics;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddElectronicsActivity extends AppCompatActivity implements OnItemSelectedListener {

    private ActivityAddElectronicsBinding binding;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    private List<Uri> uriImageList;
    private final String TAG = "LIA";
    private Electronics electronics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_electronics);

        binding = ActivityAddElectronicsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("product");

        dialog = new ProgressDialog(this);
        uriImageList = new ArrayList<>();
        electronics = new Electronics();

        electronics.setCategory(getIntent().getStringExtra("PRODUCT"));
        getSupportActionBar().setTitle("Add " + electronics.getCategory());

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
                .createFromResource(this, R.array.electronic_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spElectronicType.setOnItemSelectedListener(this);
        binding.spElectronicType.setAdapter(adapter);
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        String name = binding.etName.getText().toString();
        String descriptions = binding.etDescriptions.getText().toString();
        String piece = binding.etPiece.getText().toString();
        String quantity = binding.etQuantity.getText().toString();
        String brand_name  = binding.etBrandName.getText().toString();

        if(!name.isEmpty() && !piece.isEmpty() &&
                !quantity.isEmpty() && electronics.getProduct_type() != null) {

            electronics = new Electronics(id, name, descriptions, null, piece, quantity,
                    this.electronics.getCategory(), brand_name, this.electronics.getProduct_type());

            onCreateProduct(electronics);

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
                            onCreateData(electronics);
                        }
                    }
                });
            }
        } else onCreateData(electronics);
    }

    private void onCreateData(Electronics electronics) {
        dialog.setMessage("Setuping data..");

        new ProductsRepository().insertProducts(electronics).addOnSuccessListener(documentReference -> {
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
            electronics.setProduct_type(parent.getSelectedItem().toString());
        }else {
            view.setEnabled(false);
            electronics.setProduct_type(null);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}