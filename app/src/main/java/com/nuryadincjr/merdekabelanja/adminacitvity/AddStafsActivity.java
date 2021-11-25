package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.UUID;

public class AddStafsActivity extends AppCompatActivity implements OnItemSelectedListener {

    private ActivityAddStafsBinding binding;
    private StorageReference storageReference;
    private SpinnersAdapter spinnersAdapter;
    private ProgressDialog dialog;
    private Staffs staffs;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stafs);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivityAddStafsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("staffs").child("profiles");
        spinnersAdapter = SpinnersAdapter.getInstance(this);

        dialog = new ProgressDialog(this);
        staffs = new Staffs();

        binding.btnRegister.setOnClickListener(v -> getInputValidations());
        binding.btnAddPhoto.setOnCheckedChangeListener(this::onCheckedChange);

        spinnersAdapter.getSpinnerAdapter(binding.spDevisions, R.array.devision);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getInputValidations() {
        String fullname = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confpassword = binding.etConfPassword.getText().toString();
        String address = binding.etAddress.getText().toString();

        if(!fullname.isEmpty() && !phone.isEmpty() && !email.isEmpty() && !password.isEmpty() &&
                !confpassword.isEmpty() && !staffs.getDevision().equals("Select Devision")) {
            if(password.length() > 7) {
                if(password.equals(confpassword)){
                    String uniqueID = UUID.randomUUID().toString();
                    Staffs staffs = new Staffs(uniqueID, fullname, phone, email, "", address, email,
                            password, Constaint.time(), "register", this.staffs.getDevision());

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

                    onCreateData(staffs);
                }
            });
        } else onCreateData(staffs);
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

    private void onCheckedChange(CompoundButton compoundButton, boolean isChecked) {
        if (isChecked) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, 25);
        } else {
            binding.ivPhoto.setVisibility(View.GONE);
            imageUri = null;
        }
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(position ==0 )view.setEnabled(false);
        staffs.setDevision(parent.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        staffs.setDevision(parent.getSelectedItem().toString());
    }
}