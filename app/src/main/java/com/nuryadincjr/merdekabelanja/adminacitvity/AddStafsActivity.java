package com.nuryadincjr.merdekabelanja.adminacitvity;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityAddStafsBinding;
import com.nuryadincjr.merdekabelanja.models.Staffs;

import java.util.UUID;

public class AddStafsActivity extends AppCompatActivity {

    private ActivityAddStafsBinding binding;
    private StorageReference storageReference;
    private ProgressDialog dialog;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stafs);

        binding = ActivityAddStafsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storageReference = FirebaseStorage.getInstance().getReference().child("staffs").child("profiles");
        dialog = new ProgressDialog(this);

        binding.btnRegister.setOnClickListener(v -> getRegister());
        binding.btnAddPhoto.setOnCheckedChangeListener(this::onCheckedChanged);
    }

    private void getRegister() {
        String fullname = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confpassword = binding.etConfPassword.getText().toString();
        String address = binding.etAddress.getText().toString();

        if(!fullname.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
                !password.isEmpty() && !confpassword.isEmpty()) {
            if(password.length() > 7) {
                if(password.equals(confpassword)){
                    Staffs staffs = new Staffs("", fullname, phone, email,
                            "", address, email, password, "staff");
                    onRegister(staffs);

                } else binding.etPassword.setError("Password canot equals!");
            } else binding.etConfPassword.setError("Password too short!");
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onRegister(Staffs staffs) {
        dialog.setMessage("Register..");
        dialog.setCancelable(false);
        dialog.show();

        String uniqueID = UUID.randomUUID().toString();
        staffs.setUid(uniqueID);

        if(imageUri != null) {
            dialog.setMessage("Uploading file..");

            StorageReference filePath = storageReference.child(uniqueID)
                    .child(uniqueID + "." + getFileExtension(imageUri));
            StorageTask<UploadTask.TaskSnapshot> uploadTask = filePath.putFile(imageUri);

            uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()) throw task.getException(); dialog.dismiss();
                return filePath.getDownloadUrl();
            }).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    dialog.setMessage("Setuping profile..");

                    Uri downdoadUri = task.getResult();
                    staffs.setPhoto(downdoadUri.toString());

                    new StaffsRepository().insertStaffs(staffs).addOnSuccessListener(documentReference -> {
                        dialog.dismiss();
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
                        Toast.makeText(AddStafsActivity.this.getApplicationContext(),
                                "Success.", Toast.LENGTH_SHORT).show();
                        AddStafsActivity.this.finish();
                    }).addOnFailureListener(e -> {
                        dialog.dismiss();
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(AddStafsActivity.this.getApplicationContext(),
                                "Error adding document.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else{
            dialog.setMessage("Setuping profile..");

            new StaffsRepository().insertStaffs(staffs).addOnSuccessListener(documentReference -> {
                dialog.dismiss();
                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
                Toast.makeText(AddStafsActivity.this.getApplicationContext(),
                        "Success.", Toast.LENGTH_SHORT).show();
                AddStafsActivity.this.finish();
            }).addOnFailureListener(e -> {
                dialog.dismiss();
                Log.w(TAG, "Error adding document", e);
                Toast.makeText(AddStafsActivity.this.getApplicationContext(),
                        "Error adding document.", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (b) {
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

    private String getFileExtension(Uri imageUri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getContentResolver().getType(imageUri));
    }
}