package com.nuryadincjr.merdekabelanja.activity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ACTION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISLOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_LOGIN;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.AdminsRepository;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityLoginBinding;
import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.models.Users;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private String isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isLogin = getIntent().getStringExtra(NAME_LOGIN);
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Sign in");
        dialog.setCancelable(false);

        getLogin();
        getInputValidations();
    }

    @SuppressLint("SetTextI18n")
    private void getLogin() {
        switch (isLogin) {
            case "ADMIN":
            case "STAFF":
                binding.btnLogin.setText("LOG IN AS " + isLogin);
                break;
            case "USER":
                binding.btnLogin.setText("LOG IN");
                binding.linearLayout.setVisibility(View.VISIBLE);
                binding.tiLayout.setVisibility(View.GONE);
                binding.ccp.registerCarrierNumberEditText(binding.etPhone);
                binding.ccp.setCountryForNameCode("ID");
                break;
        }
    }

    private void getInputValidations() {
        binding.btnLogin.setOnClickListener(v -> {
            String username = String.valueOf(binding.etUsername.getText());
            String password = String.valueOf(binding.etPassword.getText());
            
            boolean isPhoneCurtly = false;
            if(isLogin.equals("USER")){
                username = binding.ccp.getFullNumberWithPlus();
                isPhoneCurtly = binding.ccp.isValidFullNumber();
            }
            
            if(!username.isEmpty() && !password.isEmpty()){
                if(isLogin.equals("USER")){
                    if (isPhoneCurtly) {
                        onLogin(username, password);
                    } else binding.etPhone.setError("Phone number cannot found!");
                }else onLogin(username, password);
            } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
        });
    }

    private void onLogin(String username, String password) {
        switch (isLogin) {
            case "USER":
                Users user = new Users();
                user.setPhone(username);
                user.setPassword(password);
                new UsersRepository().getUserLogin(user).observe(this, this::getStartActivity);
                break;
            case "ADMIN":
                Admins admin = new Admins();
                admin.setUsername(username);
                admin.setPassword(password);
                new AdminsRepository().getAdminLogin(admin).observe(this, this::getStartActivity);
                break;
            case "STAFF":
                Staffs staff = new Staffs();
                staff.setUsername(username);
                staff.setPassword(password);
                new StaffsRepository().getStaffLogin(staff).observe(this, this::getStartActivity);
                break;
        }switch (isLogin) {
            case "USER":
                Users user = new Users();
                user.setPhone(username);
                user.setPassword(password);
                new UsersRepository().getUserLogin(user).observe(this, this::getStartActivity);
                break;
            case "ADMIN":
                Admins admin = new Admins();
                admin.setUsername(username);
                admin.setPassword(password);
                new AdminsRepository().getAdminLogin(admin).observe(this,this::getStartActivity);
                break;
            case "STAFF":
                Staffs staff = new Staffs();
                staff.setUsername(username);
                staff.setPassword(password);
                new StaffsRepository().getStaffLogin(staff).observe(this, this::getStartActivity);
                break;
        }
    }

    private <T> void getStartActivity(ArrayList<T> tArrayList) {
        if (tArrayList.size() != 0) {
            startActivity(new Intent(this, OTPActivity.class)
                    .putExtra(NAME_LOGIN, (Parcelable) tArrayList.get(0))
                    .putExtra(NAME_ACTION, "LOGIN")
                    .putExtra(NAME_ISLOGIN, isLogin));
        } else Toast.makeText(this, "Your username or password wrong!",
                Toast.LENGTH_SHORT).show();
    }
}