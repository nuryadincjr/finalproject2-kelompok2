package com.nuryadincjr.merdekabelanja.usrsactivity;

import static android.content.ContentValues.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_PROFILE;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_USER;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getInfo;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityMyInfoBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;

import java.util.ArrayList;

public class MyInfoActivity extends AppCompatActivity {

    private ActivityMyInfoBinding binding;
    private ImagesPreference imagesPreference;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    private Uri imageUri;
    private Users users;
    private Menu menu;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityMyInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocalPreference localPreference = LocalPreference.getInstance(this);
        storageReference = FirebaseStorage.getInstance().getReference()
                .child(CHILD_USER).child(CHILD_PROFILE);
        uid = localPreference.getPreferences().getString(KEY_UID, "");
        imagesPreference = ImagesPreference.getInstance(this);
        dialog = new ProgressDialog(this);

        binding.btnAddPhoto.setOnClickListener(v -> imagesPreference.getSinggleImage(this));

        onSetData();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user, menu);
        this.menu = menu;

        isEdited(false);
        return super.onCreatePanelMenu(featureId, menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemEdit:
                isEdited(true);
                return true;
            case R.id.itemHelp:
                getInfo(this);
                return true;
            case R.id.itemSaves:
                getInputValidations();
                return true;
            case R.id.itemCencle:
                onSetData();
                isEdited(false);
                return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void isEdited(boolean isEdited) {
        binding.etName.setEnabled(isEdited);
        binding.etEmail.setEnabled(isEdited);
        binding.etOrderAddress.setEnabled(isEdited);
        binding.etDestinationAddress.setEnabled(isEdited);
        binding.btnAddPhoto.setEnabled(isEdited);

        menu.setGroupVisible(R.id.groupTools, isEdited);
        menu.findItem(R.id.itemMore).setVisible(!isEdited);
    }

    private void getInputValidations() {
        String name = String.valueOf(binding.etName.getText());
        String email = String.valueOf(binding.etEmail.getText());
        String orderAdd = String.valueOf(binding.etOrderAddress.getText());
        String destAdd = String.valueOf(binding.etDestinationAddress.getText());
        if(!name.isEmpty()) {
            users.setName(name);
            users.setEmail(email);
            users.setAddress(orderAdd);
            users.setAddress2(destAdd);
            users.setLatest_update(time());
            onUpdateData(users);
        } else Toast.makeText(this, "Please input your name!", Toast.LENGTH_SHORT).show();
    }

    private void onUpdateData(Users users) {
        dialog.setMessage("Register..");
        dialog.setCancelable(false);
        dialog.show();

        if(imageUri != null) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(users.getUid())
                    .child(users.getUid() + "." + getFileExtension(imageUri, this));
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
                    users.setPhoto(downdoadUri.toString());
                    onCreateData(users);
                }
            });
        } else onCreateData(users);
    }

    private void onCreateData(Users users) {
        dialog.setMessage("Updating profile");
        dialog.show();
        new UsersRepository().updateUser(users).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(this,"Success.", Toast.LENGTH_SHORT).show();
            isEdited(false);
            dialog.dismiss();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(this, "Error adding document.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
    }

    private void onSetData() {
        new UsersRepository().getUserData(uid).observe(this, (ArrayList<Users> user) -> {
            if(user.size() != 0) {
                users = user.get(0);
                if(!users.getPhoto().isEmpty()) {
                    Glide.with(this)
                            .load(users.getPhoto())
                            .centerCrop()
                            .placeholder(R.drawable.ic_brand)
                            .into(binding.ivPhoto);
                    binding.ivPhoto.setVisibility(View.VISIBLE);
                    binding.btnAddPhoto.setChecked(true);
                }
                binding.etName.setText(users.getName());
                binding.etEmail.setText(users.getEmail());
                binding.etOrderAddress.setText(users.getAddress());
                binding.etDestinationAddress.setText(users.getAddress2());
            }
        });
    }
}