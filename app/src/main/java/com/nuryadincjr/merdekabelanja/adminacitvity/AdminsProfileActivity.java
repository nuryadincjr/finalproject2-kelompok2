package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;
import static com.nuryadincjr.merdekabelanja.pojo.PermissionsAccess.requestStoragePermission;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_ADMIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.CHILD_PROFILE;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getFileExtension;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import static java.util.Objects.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.AdminsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAdminsProfileBinding;
import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.ArrayList;

public class AdminsProfileActivity extends AppCompatActivity {

    private ActivityAdminsProfileBinding binding;
    private StorageReference storageReference;
    private ImagesPreference imagesPreference;
    private ProgressDialog dialog;
    private Uri imageUri;
    private Admins data;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admins_profile);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding = ActivityAdminsProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference()
                .child(CHILD_ADMIN).child(CHILD_PROFILE);
        imagesPreference = ImagesPreference.getInstance(this);
        LocalPreference localPreference = new LocalPreference(this);
        dialog = new ProgressDialog(this);

        String uid = localPreference.getPreferences().getString(KEY_UID, "");

        getData(uid);
        setFocusable();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_admin, menu);
        this.menu = menu;

        setVisibleMenu(false, true);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
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
                getDataChanged();
                return true;
            case R.id.itemClose:
                getDataCanceled();
                return true;
            case R.id.itemPrint:
                getDataPrinted();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            binding.ivPhoto.setImageURI(imageUri);
        } else imageUri = null;
    }

    private void getData(String uid) {
        new AdminsRepository().getAdmin(uid)
                .observe(this, this::onDataSet);
    }

    private void getDataCanceled() {
        getData(data.getUid());
        requireNonNull(getSupportActionBar()).setTitle("Details Admins");
        setFocusable();
        setVisibleMenu(false, true);
    }

    private void getDataEdited() {
        requireNonNull(getSupportActionBar()).setTitle("Edits Admins");
        setFocusableInTouchMode();
        setVisibleMenu(true, false);
        binding.ivPhoto.setOnClickListener(view -> imagesPreference.getSinggleImage(this));
    }

    private void getDataChanged() {
        String fullName = binding.tvName.getText().toString();
        String phone = binding.tvPhone.getText().toString();
        String email = binding.tvEmail.getText().toString();
        String address = binding.tvAddress.getText().toString();
        String userName = binding.tvUsername.getText().toString();

        if(!fullName.isEmpty() && !phone.isEmpty() &&
                !email.isEmpty() && !userName.isEmpty()) {
            Admins admins = new Admins(data.getUid(), fullName, phone, email,
                    "", address, email, data.getPassword(), time(),
                    "register");
            onDataChange(admins);
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void getDataPrinted() {
        if ((ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) ||
                (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
            Snackbar.make(binding.getRoot(),
                    R.string.storage_permission_available,
                    Snackbar.LENGTH_SHORT).show();
            startPrinting();
        } else {
            requestStoragePermission(this, binding.getRoot());
        }
    }

    private void startPrinting() {
        if(binding.getRoot().getWidth() != 0 &&
                binding.getRoot().getHeight() !=0){
            PdfConverters.getInstance(this)
                    .getDataToPdf(binding.getRoot(), data.getUid());
        }
    }

    private void setVisibleMenu(boolean visible1, boolean visible2) {
        menu.findItem(R.id.itemSaves).setVisible(visible1);
        menu.findItem(R.id.itemClose).setVisible(visible1);
        menu.findItem(R.id.itemEdit).setVisible(visible2);
        menu.findItem(R.id.itemPrint).setVisible(visible2);
    }

    private void setFocusable() {
        binding.tvId.setFocusable(false);
        binding.tvName.setFocusable(false);
        binding.tvPhone.setFocusable(false);
        binding.tvEmail.setFocusable(false);
        binding.tvAddress.setFocusable(false);
        binding.tvUsername.setFocusable(false);
        binding.tvPassword.setFocusable(false);
        binding.ivPhoto.setEnabled(false);
    }

    private void setFocusableInTouchMode() {
        binding.tvName.setFocusableInTouchMode(true);
        binding.tvPhone.setFocusableInTouchMode(true);
        binding.tvEmail.setFocusableInTouchMode(true);
        binding.tvAddress.setFocusableInTouchMode(true);
        binding.tvUsername.setFocusableInTouchMode(true);
        binding.tvPassword.setFocusableInTouchMode(true);
        binding.ivPhoto.setEnabled(true);
    }

    private void onDataSet(ArrayList<Admins> admin) {
        if (admin.size() != 0) {
            data = admin.get(0);

            String url = admin.get(0).getPhoto();
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);

            binding.tvId.setText(admin.get(0).getUid());
            binding.tvName.setText(admin.get(0).getName());
            binding.tvPhone.setText(admin.get(0).getPhone());
            binding.tvEmail.setText(admin.get(0).getEmail());
            binding.tvAddress.setText(admin.get(0).getAddress());
            binding.tvUsername.setText(admin.get(0).getUsername());
            binding.tvAccount.setText(admin.get(0).getStatus_account());
            binding.tvLatestUpdate.setText(admin.get(0).getLatest_update());
        }
    }

    private void onDataChange(Admins admins) {
        dialog.setMessage("Change data..");
        dialog.setCancelable(false);
        dialog.show();

        if(imageUri != null) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(admins.getUid())
                    .child(admins.getUid() + "." + getFileExtension(imageUri, this));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) {
                    throw requireNonNull(task.getException());
                } dialog.dismiss();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Uri downdoadUri = task.getResult();
                    assert downdoadUri != null;
                    admins.setPhoto(downdoadUri.toString());

                    onCreateData(admins);
                }
            });
        } else onCreateData(admins);
    }

    private void onCreateData(Admins admins) {
        dialog.setMessage("Setup profile..");

        new AdminsRepository().updateAdmins(admins).addOnSuccessListener(documentReference -> {
            dialog.dismiss();
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(),
                    "Success.", Toast.LENGTH_SHORT).show();

            requireNonNull(getSupportActionBar()).setTitle("Details Staff");
            setFocusable();
            setVisibleMenu(false, true);

        }).addOnFailureListener(e -> {
            dialog.dismiss();
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(),
                    "Error adding document.", Toast.LENGTH_SHORT).show();
        });
    }
}