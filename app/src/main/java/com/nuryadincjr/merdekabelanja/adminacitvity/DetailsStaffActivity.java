package com.nuryadincjr.merdekabelanja.adminacitvity;

import static com.nuryadincjr.merdekabelanja.pojo.PermissionsAccess.requestStoragePermission;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;

import static java.util.Objects.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetailsStaffBinding;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.ArrayList;

public class DetailsStaffActivity extends AppCompatActivity {
    private ActivityDetailsStaffBinding binding;
    private FirebaseStorage storage;
    private Staffs data;
    private boolean isPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_staff);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details Staff");

        binding = ActivityDetailsStaffBinding.inflate(getLayoutInflater());
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
            case R.id.itemDelete:
                getDataDeleted();
                return true;
            case R.id.itemPrint:
                getDataPrinted();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(Staffs staffs) {
        new StaffsRepository().getStaffLogin(staffs)
                .observe(this, this::onDataSet);
    }

    private void onDataSet(ArrayList<Staffs> staff) {
        if (staff.size() != 0) {
            data = staff.get(0);

            String url = staff.get(0).getPhoto();
            Glide.with(this)
                    .load(url)
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);

            binding.tvId.setText(staff.get(0).getUid());
            binding.tvDevision.setText(staff.get(0).getDivision());
            binding.tvName.setText(staff.get(0).getName());
            binding.tvPhone.setText(staff.get(0).getPhone());
            binding.tvEmail.setText(staff.get(0).getEmail());
            binding.tvAddress.setText(staff.get(0).getAddress());
            binding.tvUsername.setText(staff.get(0).getUsername());
            binding.tvAccount.setText(staff.get(0).getStatus_account());
            binding.tvLatestUpdate.setText(staff.get(0).getLatest_update());

            if (isPrint) getDataPrinted();
        }
    }

    private void getDataDeleted() {
        new StaffsRepository().deleteStaffs(data.getUid()).addOnSuccessListener(unused -> {
            if (data.getPhoto() != null){
                storage.getReferenceFromUrl(data.getPhoto()).delete();
            }
        });
        finish();
    }

    private void getDataEdited() {
        startActivity(new Intent(this, AddStaffsActivity.class)
                .putExtra(NAME_DATA, data)
                .putExtra(NAME_ISEDIT, true));
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
}