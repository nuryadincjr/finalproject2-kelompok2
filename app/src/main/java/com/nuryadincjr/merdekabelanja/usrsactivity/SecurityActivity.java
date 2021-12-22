package com.nuryadincjr.merdekabelanja.usrsactivity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ACTION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_EDITED;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getInfo;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.activity.OTPActivity;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivitySecurityBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

public class SecurityActivity extends AppCompatActivity {
    private ActivitySecurityBinding binding;
    private Users users;
    private Menu menu;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        binding = ActivitySecurityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LocalPreference localPreference = LocalPreference.getInstance(this);
        uid = localPreference.getPreferences().getString(KEY_UID, "");

        if(savedInstanceState == null) onDateSet();
    }

    @Override
    protected void onResume() {
        onDateSet();
        super.onResume();
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
            case R.id.itemClose:
                onDateSet();
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
                    if((!phone.equals(users.getPhone()) && !confpassword.equals(users.getPassword())) ||
                            (!phone.equals(users.getPhone()) || !confpassword.equals(users.getPassword()))){
                        if(phone.equals(users.getPhone())){
                            onDataEdited(phone, confpassword);
                        }else {
                            MainViewModel mainViewModel = new
                                    ViewModelProvider(this).get(MainViewModel.class);
                            mainViewModel.getUserPhone(phone).observe(this, user -> {
                                if(user.size() ==0){
                                    onDataEdited(phone, confpassword);
                                }else binding.etPhone.setError("This phone number is already in use!");
                            });
                        }
                    } else isEdited(false);
                } else binding.etPassword.setError("Password cannot equals!");
            } else binding.etConfPassword.setError("Password too short!");
        } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onDataEdited(String phone, String confpassword) {
        users.setPhone(phone);
        users.setPassword(confpassword);
        users.setLatest_update(time());

        startActivity(new Intent(this, OTPActivity.class)
                .putExtra(NAME_EDITED, users)
                .putExtra(NAME_ACTION, "SECURITY"));
    }

    private void onDateSet() {
        new UsersRepository().getUserData(uid).observe(this, user -> {
            if (user.size() != 0) {
                users = user.get(0);
                binding.etPhone.setText(users.getPhone());
                binding.etPassword.setText(users.getPassword());
                binding.etConfPassword.setText(users.getPassword());
            }
        });
    }
}