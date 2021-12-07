package com.nuryadincjr.merdekabelanja.usrsactivity;

import static android.content.ContentValues.TAG;

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
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;

import java.util.ArrayList;

public class SecurityActivity extends AppCompatActivity {
    private ActivitySecurityBinding binding;
    private LocalPreference localPreference;
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

        localPreference = LocalPreference.getInstance(this);
        uid = localPreference.getPreferences().getString("UID", "");
        dialog = new ProgressDialog(this);

        onDataSet();
    }

    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit_user, menu);
        this.menu = menu;

        isEdited(false);
        return super.onCreatePanelMenu(featureId, menu);
    }

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
                Toast.makeText(this, "Requerment Info", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.itemSaves:
                getInputValidations();
                return true;
            case R.id.itemCencle:
                onDataSet();
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
        String phone = binding.etPhone.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confpassword = binding.etConfPassword.getText().toString();
        if(!phone.isEmpty() && !password.isEmpty() && !confpassword.isEmpty()) {
            if(password.length() > 7) {
                if(password.equals(confpassword)){
                    users.setPhone(phone);
                    users.setPassword(confpassword);
                    users.setLatest_update(Constaint.time());
                    onDataEdied(users);
                } else binding.etPassword.setError("Password canot equals!");
            } else binding.etConfPassword.setError("Password too short!");
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onDataEdied(Users users) {
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

    private void onDataSet() {
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