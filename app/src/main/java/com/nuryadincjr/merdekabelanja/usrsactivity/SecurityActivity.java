package com.nuryadincjr.merdekabelanja.usrsactivity;

import static android.content.ContentValues.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getInfo;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivitySecurityBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;

import java.util.ArrayList;

public class SecurityActivity extends AppCompatActivity {
    private ActivitySecurityBinding binding;
    private ProgressDialog dialog;
    private Users users;
    private Menu menu;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocalPreference localPreference = LocalPreference.getInstance(this);
        uid = localPreference.getPreferences().getString(KEY_UID, "");
        dialog = new ProgressDialog(this);

        if(savedInstanceState == null) onSetData();
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

    private void isEdited(boolean isEdited) {
        binding.etPhone.setEnabled(isEdited);
        binding.etPassword.setEnabled(isEdited);
        binding.etConfPassword.setEnabled(isEdited);

        menu.setGroupVisible(R.id.groupTools, isEdited);
        menu.findItem(R.id.itemMore).setVisible(!isEdited);
    }

    private void getInputValidations() {
        String phone = String.valueOf(binding.etPhone.getText());
        String password = String.valueOf(binding.etPassword.getText());
        String confpassword = String.valueOf(binding.etConfPassword.getText());
        if(!phone.isEmpty() && !password.isEmpty() && !confpassword.isEmpty()) {
            if(password.length() > 7) {
                if(password.equals(confpassword)){
                    users.setPhone(phone);
                    users.setPassword(confpassword);
                    users.setLatest_update(time());
                    onEditData(users);
                } else binding.etPassword.setError("Password canot equals!");
            } else binding.etConfPassword.setError("Password too short!");
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onEditData(Users users) {
        dialog.setMessage("Updating account");
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
                binding.etPhone.setText(users.getPhone());
                binding.etPassword.setText(users.getPassword());
                binding.etConfPassword.setText(users.getPassword());
            }
        });
    }
}