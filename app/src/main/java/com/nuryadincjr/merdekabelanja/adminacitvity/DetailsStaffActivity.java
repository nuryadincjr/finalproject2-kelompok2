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
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetailsStaffBinding;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;

import java.util.ArrayList;

public class DetailsStaffActivity extends AppCompatActivity {

    private ActivityDetailsStaffBinding binding;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    private ProgressDialog dialog;
    private Uri imageUri;
    private Staffs data;
    private Menu menu;
    private boolean isEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_staff);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityDetailsStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("staffs").child("profiles");
        dialog = new ProgressDialog(this);

        data = getIntent().getParcelableExtra("DATA");
        isEdit = getIntent().getBooleanExtra("ISEDIT", false);

        onSetData(data);
        setFocusable(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        this.menu = menu;

        setVisibleMenu(false, true);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(isEdit) getDataEdited();
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.act_edit:
                getDataEdited();
                return true;
            case R.id.act_saves:
                getDataChange();
                return true;
            case R.id.act_cencle:
                getDataCencled();
                return true;
            case R.id.act_delete:
                getDataDelete();
                return true;
            case R.id.act_print:

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataDelete() {
        new StaffsRepository().deleteStaffs(data.getUid()).addOnSuccessListener(unused -> {
            if (data.getPhoto() != null){
               storage.getReferenceFromUrl(data.getPhoto()).delete();
            }
        });
        finish();
    }

    private void getDataCencled() {
        onSetData(data);
        getSupportActionBar().setTitle("Details Staff");
        setFocusable(false);
        setVisibleMenu(false, true);
    }

    private void getDataEdited() {
        getSupportActionBar().setTitle("Edits Staff");
        setFocusableInTouchMode(true);
        setVisibleMenu(true, false);
        binding.ivPhoto.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 25);
        });
    }

    private void getDataChange() {
        String fullname = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();
        String address = binding.etAddress.getText().toString();
        String etUsername = binding.etUsername.getText().toString();


        if(!fullname.isEmpty() && !phone.isEmpty() &&
                !email.isEmpty() && !etUsername.isEmpty()) {
            Staffs staffs = new Staffs(data.getUid(), fullname, phone, email,
                    "", address, email, data.getPassword(), Constaint.time(),
                    "register", "staff");
            onDataChange(staffs);

        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void setVisibleMenu(boolean visible1, boolean visible2) {
        menu.findItem(R.id.act_saves).setVisible(visible1);
        menu.findItem(R.id.act_cencle).setVisible(visible1);
        menu.findItem(R.id.act_edit).setVisible(visible2);
        menu.findItem(R.id.act_delete).setVisible(visible2);
        menu.findItem(R.id.act_print).setVisible(visible2);
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
//        binding.etId.setFocusableInTouchMode(isFocusable);
        binding.etName.setFocusableInTouchMode(isFocusable);
        binding.etPhone.setFocusableInTouchMode(isFocusable);
        binding.etEmail.setFocusableInTouchMode(isFocusable);
        binding.etAddress.setFocusableInTouchMode(isFocusable);
        binding.etUsername.setFocusableInTouchMode(isFocusable);
        binding.tvPawword.setFocusableInTouchMode(isFocusable);
        binding.ivPhoto.setEnabled(isFocusable);
    }

    private void onSetData(Staffs staffs) {
        new StaffsRepository().getStaffLogin(staffs).observe(this, (ArrayList<Staffs> staff) -> {
            if(staff.size() != 0) {
                data = staff.get(0);

                String url = staff.get(0).getPhoto();
                Glide.with(this)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);

                binding.etId.setText(staff.get(0).getUid());
                binding.etName.setText(staff.get(0).getName());
                binding.etPhone.setText(staff.get(0).getPhone());
                binding.etEmail.setText(staff.get(0).getEmail());
                binding.etAddress.setText(staff.get(0).getAddress());
                binding.etUsername.setText(staff.get(0).getUsername());
                binding.tvAccount.setText(staff.get(0).getStatus_account());
                binding.tvLatestUpdate.setText(staff.get(0).getLatest_update());
            }
        });
    }

    private void onDataChange(Staffs staffs) {
        dialog.setMessage("Change data..");
        dialog.setCancelable(false);
        dialog.show();

        if(imageUri != null) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(staffs.getUid())
                    .child(staffs.getUid() + "." + Constaint.getFileExtension(imageUri, this));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) throw task.getException(); dialog.dismiss();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    dialog.setMessage("Setuping profile..");

                    Uri downdoadUri = task.getResult();
                    staffs.setPhoto(downdoadUri.toString());

                    new StaffsRepository().updateStaffs(staffs).addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
                        Toast.makeText(getApplicationContext(),
                                "Success.", Toast.LENGTH_SHORT).show();

                        getSupportActionBar().setTitle("Details Staff");
                        setFocusable(false);
                        setVisibleMenu(false, true);

                        dialog.dismiss();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(getApplicationContext(),
                                "Error adding document.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else{
            dialog.setMessage("Setuping profile..");

            new StaffsRepository().updateStaffs(staffs).addOnSuccessListener(documentReference -> {
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