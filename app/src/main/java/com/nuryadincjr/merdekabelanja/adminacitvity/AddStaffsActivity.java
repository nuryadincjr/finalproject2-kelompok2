package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_PROFILE;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_STAFF;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;
import static java.lang.String.valueOf;
import static java.util.Objects.requireNonNull;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ImageViewerAdapter;
import com.nuryadincjr.merdekabelanja.adapters.SpinnersAdapter;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddStafsBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddStaffsActivity extends AppCompatActivity {

    private ActivityAddStafsBinding binding;
    private StorageReference storageReference;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private Staffs staffs;
    private boolean isEdit;
    private String imageOld;
    private String emailOld;
    private List<Uri> uriImageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stafs);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddStafsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference()
                .child(CHILD_STAFF).child(CHILD_PROFILE);
        SpinnersAdapter spinnersAdapter = SpinnersAdapter.getInstance(this);
        imagesPreference = ImagesPreference.getInstance(this);

        dialog = new ProgressDialog(this);
        staffs = new Staffs();
        isEdit = getIntent().getBooleanExtra(NAME_ISEDIT, false);
        uriImageList = new ArrayList<>();

        binding.btnRegister.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getSinggleImage(this));

        String titleBar = "Add Staff";
        titleBar = getEdited(titleBar);

        requireNonNull(getSupportActionBar()).setTitle(titleBar);
        spinnersAdapter.getSpinnerAdapter(binding.actDevisions, R.array.division , staffs.getDivision());
    }

    @SuppressLint("SetTextI18n")
    private String getEdited(String titleBar) {
        if(isEdit) {
            staffs = getIntent().getParcelableExtra(NAME_DATA);
            imageOld = staffs.getPhoto();
            emailOld = staffs.getEmail();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 && data != null) {
            uriImageList.add(data.getData());
        }

        getImageViewerAdapter();
    }

    private void getImageViewerAdapter() {
        uriImageList.remove(Uri.parse(""));
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
                staffs.setPhoto("");
                imageViewerAdapter.notifyDataSetChanged();
                binding.btnAddPhoto.setEnabled(uriImageList.size() < 1);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });
        binding.btnAddPhoto.setEnabled(uriImageList.size() < 1);
    }
    private void onDataSet(Staffs staff) {
        uriImageList.add(Uri.parse(staff.getPhoto()));
        getImageViewerAdapter();

        binding.etName.setText(staff.getName());
        binding.etPhone.setText(staff.getPhone());
        binding.etEmail.setText(staff.getEmail());
        binding.etAddress.setText(staff.getAddress());
        binding.etPassword.setText(staff.getPassword());
        binding.etConfPassword.setText(staff.getPassword());
    }

    private void getInputValidations() {
        String id = UUID.randomUUID().toString();
        String photo = "";
        if(isEdit) {
            id = staffs.getUid();
            photo = staffs.getPhoto();
        }
        String fullName = valueOf(binding.etName.getText());
        String phone = valueOf(binding.etPhone.getText());
        String email = valueOf(binding.etEmail.getText());
        String password = valueOf(binding.etPassword.getText());
        String confpassword = valueOf(binding.etConfPassword.getText());
        String address = valueOf(binding.etAddress.getText());
        String division = valueOf(binding.actDevisions.getText());

        if(phone.contains("+")){
            if(!fullName.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
                    !password.isEmpty() && !confpassword.isEmpty() && !division.isEmpty()) {
                if(password.length() > 7) {
                    if(password.equals(confpassword)){
                        Staffs staffs = new Staffs(id, fullName, phone, email, photo, address, email,
                                password, time(), "register", division);

                        onRegister(staffs);

                    } else binding.etPassword.setError("Password cannot equals!");
                } else binding.etConfPassword.setError("Password too short!");
            } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
        }else binding.etPhone.setError("Please used the country code!");
    }

    private void onRegister(Staffs staffs) {
        dialog.setMessage("Register..");
        dialog.setCancelable(false);
        dialog.show();

        if(!uriImageList.isEmpty() && !valueOf(uriImageList.get(0)).equals(this.staffs.getPhoto())) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(staffs.getUid())
                    .child(staffs.getUid() + "." + getFileExtension(uriImageList.get(0), this));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(uriImageList.get(0));

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) {
                    dialog.dismiss();
                    throw requireNonNull(task.getException());
                }
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    assert downloadUri != null;
                    staffs.setPhoto(downloadUri.toString());

                    if(isEdit) onDataUpdated(staffs);
                    else onDataCreated(staffs);
                }
            });
        } else {
            if(isEdit) onDataUpdated(staffs);
            else onDataCreated(staffs);
        }


    }

    private void onDataCreated(Staffs staffs) {
        dialog.setMessage("Setup profile..");

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getUsername(staffs.getUsername()).observe(this, user -> {
            if(user.size() ==0){
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
            }else {
                dialog.dismiss();
                binding.etEmail.setError("The email for the login session with this username already exists!");
            }
        });
    }

    private void onDataUpdated(Staffs staffs) {
        if (staffs.getPhoto().equals("") && !imageOld.equals(staffs.getPhoto())) {
            FirebaseStorage storage = FirebaseStorage.getInstance();
            storage.getReferenceFromUrl(imageOld).delete().addOnCompleteListener(task -> {
                if(task.isSuccessful())startDataUpdated(staffs);
            });
        } else startDataUpdated(staffs);
    }

    private void startDataUpdated(Staffs staffs) {
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getUsername(staffs.getUsername()).observe(this, user -> {
            if(user.size() ==0){
                getDataUpdated(staffs);
            }else {
                if(user.get(0).getUsername().equals(emailOld)){
                    getDataUpdated(staffs);
                }else {
                    dialog.dismiss();
                    binding.etEmail.setError("The email for the login session with this username already exists!");
                }
            }
        });
    }

    private void getDataUpdated(Staffs staffs) {
        new StaffsRepository().updateStaffs(staffs).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            dialog.dismiss();
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(),
                    "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }
}