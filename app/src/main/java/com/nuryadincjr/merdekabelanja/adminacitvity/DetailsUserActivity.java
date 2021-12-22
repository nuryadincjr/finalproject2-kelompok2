package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.pojo.PermissionsAccess.requestStoragePermission;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;
import static java.util.Objects.requireNonNull;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetailsUserBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.ArrayList;

public class DetailsUserActivity extends AppCompatActivity {
    private ActivityDetailsUserBinding binding;
    private FirebaseStorage storage;
    private Users data;
    private boolean isPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_user);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details Users");

        binding = ActivityDetailsUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        data = getIntent().getParcelableExtra(NAME_DATA);
        isPrint = getIntent().getBooleanExtra(NAME_ISPRINT, false);
    }

    @Override
    protected void onResume() {
        getData(data);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        menu.findItem(R.id.itemSaves).setVisible(false);
        menu.findItem(R.id.itemClose).setVisible(false);
        menu.findItem(R.id.itemEdit).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemDelete:
                getDataDeleted();
                return true;
            case R.id.itemPrint:
                getDataPrinted();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(Users users) {
        new UsersRepository().getUserData(users.getUid()).observe(this, this::onDataSet);
    }

    private void getDataDeleted() {
        new UsersRepository().deleteUser(data.getUid()).addOnSuccessListener(unused -> {
            if (data.getPhoto() != null){
                storage.getReferenceFromUrl(data.getPhoto()).delete();
            }
        });
        finish();
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

    private void onDataSet(ArrayList<Users> user) {
        if (user.size() != 0) {
            data = user.get(0);

            String url = user.get(0).getPhoto();
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);

            binding.tvId.setText(user.get(0).getUid());
            binding.tvName.setText(user.get(0).getName());
            binding.tvPhone.setText(user.get(0).getPhone());
            binding.tvEmail.setText(user.get(0).getEmail());
            binding.tvAddress.setText(user.get(0).getAddress());
            binding.tvAddress2.setText(user.get(0).getAddress2());
            binding.tvUsername.setText(user.get(0).getUsername());
            binding.tvAccount.setText(user.get(0).getStatus_account());
            binding.tvLatestUpdate.setText(user.get(0).getLatest_update());

            if (isPrint) getDataPrinted();
        }
    }
}