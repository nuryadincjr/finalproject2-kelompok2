package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_PROFILE;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_STAFF;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import android.annotation.SuppressLint;
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
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;

import java.util.UUID;

public class AddStaffsActivity extends AppCompatActivity {

    private ActivityAddStafsBinding binding;
    private StorageReference storageReference;
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

        storageReference = FirebaseStorage.getInstance().getReference()
                .child(CHILD_STAFF).child(CHILD_PROFILE);
        SpinnersAdapter spinnersAdapter = SpinnersAdapter.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);

        dialog = new ProgressDialog(this);
        staffs = new Staffs();
        isEdit = getIntent().getBooleanExtra(NAME_ISEDIT, false);

        binding.btnRegister.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getSinggleImage(this));

        String titleBar = "Add Staff";
        titleBar = getIsEdited(titleBar);

        getSupportActionBar().setTitle(titleBar);
        spinnersAdapter.getSpinnerAdapter(binding.actDevisions, R.array.division , staffs.getDivision());
    }

    @SuppressLint("SetTextI18n")
    private String getIsEdited(String titleBar) {
        if(isEdit) {
            staffs = getIntent().getParcelableExtra(NAME_DATA);
            onDataSet(staffs);
            binding.btnRegister.setText("Save Data");
            titleBar = "Edit Staff";
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

    private void onDataSet(Staffs staff) {
        if(!staff.getPhoto().isEmpty()) {
            Glide.with(this)
                        .load(staff.getPhoto())
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);
            binding.ivPhoto.setVisibility(View.VISIBLE);
            binding.btnAddPhoto.setChecked(true);
        }

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
        String fullName = String.valueOf(binding.etName.getText());
        String phone = String.valueOf(binding.etPhone.getText());
        String email = String.valueOf(binding.etEmail.getText());
        String password = String.valueOf(binding.etPassword.getText());
        String confpassword = String.valueOf(binding.etConfPassword.getText());
        String address = String.valueOf(binding.etAddress.getText());
        String division = String.valueOf(binding.actDevisions.getText());

        if(!fullName.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
                !password.isEmpty() && !confpassword.isEmpty() && !division.isEmpty()) {
            if(password.length() > 7) {
                if(password.equals(confpassword)){
                    Staffs staffs = new Staffs(id, fullName, phone, email, "", address, email,
                            password, time(), "register", division);

                    onRegister(staffs);

                } else binding.etPassword.setError("Password cannot equals!");
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
                    .child(staffs.getUid() + "." + getFileExtension(imageUri, this));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) {
                    dialog.dismiss();
                    throw task.getException();
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    staffs.setPhoto(downloadUri.toString());

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
        dialog.setMessage("Setup profile..");

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
        dialog.setMessage("Setup profile..");

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