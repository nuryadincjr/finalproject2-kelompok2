package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.SpinnersAdapter;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddStafsBinding;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;

import java.util.UUID;

public class AddStafsActivity extends AppCompatActivity {

    private ActivityAddStafsBinding binding;
    private StorageReference storageReference;
    private SpinnersAdapter spinnersAdapter;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private Staffs staffs;
    private Uri imageUri;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stafs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddStafsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("staffs").child("profiles");
        spinnersAdapter = SpinnersAdapter.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);

        dialog = new ProgressDialog(this);
        staffs = new Staffs();
        isEdit = getIntent().getBooleanExtra("ISEDIT", false);

        binding.btnRegister.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getSinggleImage(this));

        String titleBar = "Add Staff";
        if(isEdit) {
            staffs = getIntent().getParcelableExtra("DATA");
            onDataSet(staffs);
            binding.btnRegister.setText("Save Data");
            titleBar = "Edit Staff";
        }

        getSupportActionBar().setTitle(titleBar);
        spinnersAdapter.getSpinnerAdapter(binding.actDevisions, R.array.devision , staffs.getDevision());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onDataSet(Staffs staff) {
            Glide.with(this)
                    .load(staff.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);

            binding.etName.setText(staff.getName());
            binding.etPhone.setText(staff.getPhone());
            binding.etEmail.setText(staff.getEmail());
            binding.etAddress.setText(staff.getAddress());
            binding.etPassword.setText(staff.getPassword());
            binding.etConfPassword.setText(staff.getPassword());
    }
    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        if(isEdit) id = staffs.getUid();
        String fullname = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confpassword = binding.etConfPassword.getText().toString();
        String address = binding.etAddress.getText().toString();
        String devision = binding.actDevisions.getText().toString();

        if(!fullname.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
                !password.isEmpty() && !confpassword.isEmpty() && !devision.isEmpty()) {
            if(password.length() > 7) {
                if(password.equals(confpassword)){
                    Staffs staffs = new Staffs(id, fullname, phone, email, "", address, email,
                            password, Constaint.time(), "register", devision);

                    onRegister(staffs);

                } else binding.etPassword.setError("Password canot equals!");
            } else binding.etConfPassword.setError("Password too short!");
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onRegister(Staffs staffs) {
        dialog.setMessage("Register..");
        dialog.setCancelable(false);
        dialog.show();

        if(imageUri != null) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(staffs.getUid())
                    .child(staffs.getUid() + "." + Constaint.getFileExtension(imageUri, this));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) {
                    dialog.dismiss();
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downdoadUri = task.getResult();
                    staffs.setPhoto(downdoadUri.toString());

                    if(isEdit) onUpdateData(staffs);
                    else onCreateData(staffs);
                }
            });
        } else {
            if(isEdit) onUpdateData(staffs);
            else onCreateData(staffs);
        }
    }

    private void onCreateData(Staffs staffs) {
        dialog.setMessage("Setuping profile..");

        new StaffsRepository().insertStaffs(staffs).addOnSuccessListener(documentReference -> {
            dialog.dismiss();
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(),
                    "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.ivPhoto.setVisibility(View.VISIBLE);
            binding.ivPhoto.setImageURI(imageUri);
        }else {
            imageUri = null;
            binding.btnAddPhoto.setChecked(false);
        }
    }
    
    private void onUpdateData(Staffs staffs) {
        dialog.setMessage("Setuping profile..");

        new StaffsRepository().updateStaffs(staffs).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(),
                    "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }
}