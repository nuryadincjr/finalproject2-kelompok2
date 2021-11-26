package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.AdminsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAdminsProfileBinding;
import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.ArrayList;

public class AdminsProfileActivity extends AppCompatActivity {

    private ActivityAdminsProfileBinding binding;
    private StorageReference storageReference;
    private LocalPreference localPreference;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private Uri imageUri;
    private Admins data;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAdminsProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("admins").child("profiles");
        imagesPreference = ImagesPreference.getInstance(this);
        localPreference = new LocalPreference(this);
        dialog = new ProgressDialog(this);

        String uid = localPreference.getPreferences().getString("UID", "");

        onSetData(uid);
        setFocusable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_admin, menu);
        this.menu = menu;

        setVisibleMenu(false, true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemEdit:
                getDataEdited();
                return true;
            case R.id.itemSaves:
                getDataChange();
                return true;
            case R.id.itemCencle:
                getDataCencled();
                return true;
            case R.id.itemPrint:
                PdfConverters.getInstance(this)
                        .getDataToPdf(binding.getRoot(), data.getUid());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSetData(String uid) {
        new AdminsRepository().getAdmin(uid).observe(this, (ArrayList<Admins> admin) -> {
            if(admin.size() != 0) {
                data = admin.get(0);

                String url = admin.get(0).getPhoto();
                Glide.with(this)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);

                binding.etId.setText(admin.get(0).getUid());
                binding.etName.setText(admin.get(0).getName());
                binding.etPhone.setText(admin.get(0).getPhone());
                binding.etEmail.setText(admin.get(0).getEmail());
                binding.etAddress.setText(admin.get(0).getAddress());
                binding.etUsername.setText(admin.get(0).getUsername());
                binding.tvAccount.setText(admin.get(0).getStatus_account());
                binding.tvLatestUpdate.setText(admin.get(0).getLatest_update());
            }
        });
    }

    private void getDataCencled() {
        onSetData(data.getUid());
        getSupportActionBar().setTitle("Details Admins");
        setFocusable(false);
        setVisibleMenu(false, true);
    }

    private void getDataEdited() {
        getSupportActionBar().setTitle("Edits Admins");
        setFocusableInTouchMode(true);
        setVisibleMenu(true, false);
        binding.ivPhoto.setOnClickListener(view -> imagesPreference.getSinggleImage(this));
    }

    private void getDataChange() {
        String fullname = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();
        String address = binding.etAddress.getText().toString();
        String etUsername = binding.etUsername.getText().toString();

        if(!fullname.isEmpty() && !phone.isEmpty() &&
                !email.isEmpty() && !etUsername.isEmpty()) {
            Admins admins = new Admins(data.getUid(), fullname, phone, email,
                    "", address, email, data.getPassword(), Constaint.time(),
                    "register");
            onDataChange(admins);
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void setVisibleMenu(boolean visible1, boolean visible2) {
        menu.findItem(R.id.itemSaves).setVisible(visible1);
        menu.findItem(R.id.itemCencle).setVisible(visible1);
        menu.findItem(R.id.itemEdit).setVisible(visible2);
        menu.findItem(R.id.itemPrint).setVisible(visible2);
    }

    private void setFocusable(boolean isFocusable) {
        binding.etId.setFocusable(isFocusable);
        binding.etName.setFocusable(isFocusable);
        binding.etPhone.setFocusable(isFocusable);
        binding.etEmail.setFocusable(isFocusable);
        binding.etAddress.setFocusable(isFocusable);
        binding.etUsername.setFocusable(isFocusable);
        binding.tvPawword.setFocusable(isFocusable);
        binding.ivPhoto.setEnabled(isFocusable);
    }

    private void setFocusableInTouchMode(boolean isFocusable) {
        binding.etName.setFocusableInTouchMode(isFocusable);
        binding.etPhone.setFocusableInTouchMode(isFocusable);
        binding.etEmail.setFocusableInTouchMode(isFocusable);
        binding.etAddress.setFocusableInTouchMode(isFocusable);
        binding.etUsername.setFocusableInTouchMode(isFocusable);
        binding.tvPawword.setFocusableInTouchMode(isFocusable);
        binding.ivPhoto.setEnabled(isFocusable);
    }

    private void onDataChange(Admins admins) {
        dialog.setMessage("Change data..");
        dialog.setCancelable(false);
        dialog.show();

        if(imageUri != null) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(admins.getUid())
                    .child(admins.getUid() + "." + Constaint.getFileExtension(imageUri, this));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) throw task.getException(); dialog.dismiss();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downdoadUri = task.getResult();
                    admins.setPhoto(downdoadUri.toString());

                    onCreateData(admins);
                }
            });
        } else onCreateData(admins);
    }

    private void onCreateData(Admins admins) {
        dialog.setMessage("Setuping profile..");

        new AdminsRepository().updateAdmins(admins).addOnSuccessListener(documentReference -> {
            dialog.dismiss();
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();

            getSupportActionBar().setTitle("Details Staff");
            setFocusable(false);
            setVisibleMenu(false, true);

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
            binding.ivPhoto.setImageURI(imageUri);
        } else imageUri = null;
    }
}