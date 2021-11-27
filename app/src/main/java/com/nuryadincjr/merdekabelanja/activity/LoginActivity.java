package com.nuryadincjr.merdekabelanja.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
    private ProgressDialog dialog;
    private String isLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        isLogin = getIntent().getStringExtra("LOGIN");

        dialog = new ProgressDialog(this);
        dialog.setMessage("Sign in");
        dialog.setCancelable(false);

        getIsLogin();
        getInputValidations();
    }

    private void getIsLogin() {
        switch (isLogin) {
            case "ADMIN":
            case "STAFF":
                binding.btnLogin.setText("LOG IN AS " + isLogin);
                break;
            case "USER":
                binding.btnLogin.setText("LOG IN");
                binding.tiLayout.setHint("Phone number");

                binding.etUsername.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
        }
    }

    private void getInputValidations() {
        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            if(!username.isEmpty() && !password.isEmpty()){
                onLogin(username, password);
            } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
        });
    }

    private void onLogin(String username, String password) {
        switch (isLogin) {
            case "USER":
                Users user = new Users();
                user.setPhone(username);
                user.setPassword(password);
                new UsersRepository().getUserLogin(user).observe(this, (ArrayList<Users> users) -> {
                    if(users.size() != 0) {
                        startActivity(new Intent(this, OTPActivity.class)
                                .putExtra("LOGIN", users.get(0))
                                .putExtra("TAG", "LOGIN")
                                .putExtra("ISLOGIN", isLogin));
                    } else Toast.makeText(this,"Your username or password wrong!",
                            Toast.LENGTH_SHORT).show();
                });
                break;
            case "ADMIN":
                Admins admin = new Admins();
                admin.setUsername(username);
                admin.setPassword(password);
                new AdminsRepository().getAdminLogin(admin).observe(this, (ArrayList<Admins> admins) -> {
                    if(admins.size() != 0) {
                        Log.d("LIA", admins.get(0).toString());
                        startActivity(new Intent(this, OTPActivity.class)
                                .putExtra("LOGIN", admins.get(0))
                                .putExtra("TAG", "LOGIN")
                                .putExtra("ISLOGIN", isLogin));
                    } else Toast.makeText(this,"Your username or password wrong!",
                            Toast.LENGTH_SHORT).show();
                });
                break;
            case "STAFF":
                Staffs staff = new Staffs();
                staff.setUsername(username);
                staff.setPassword(password);
                new StaffsRepository().getStaffLogin(staff).observe(this, (ArrayList<Staffs> staffs) -> {
                    if(staffs.size() != 0) {
                        startActivity(new Intent(this, OTPActivity.class)
                                .putExtra("LOGIN", staffs.get(0))
                                .putExtra("TAG", "LOGIN")
                                .putExtra("ISLOGIN", isLogin));
                    } else Toast.makeText(this,"Your username or password wrong!",
                            Toast.LENGTH_SHORT).show();
                });
                break;
        }switch (isLogin) {
            case "USER":
                Users user = new Users();
                user.setPhone(username);
                user.setPassword(password);
                new UsersRepository().getUserLogin(user).observe(this, (ArrayList<Users> users) -> {
                    if(users.size() != 0) {
                        startActivity(new Intent(this, OTPActivity.class)
                                .putExtra("LOGIN", users.get(0))
                                .putExtra("TAG", "LOGIN")
                                .putExtra("ISLOGIN", isLogin));
                    } else Toast.makeText(this,"Your username or password wrong!",
                            Toast.LENGTH_SHORT).show();
                });
                break;
            case "ADMIN":
                Admins admin = new Admins();
                admin.setUsername(username);
                admin.setPassword(password);
                new AdminsRepository().getAdminLogin(admin).observe(this, (ArrayList<Admins> admins) -> {
                    if(admins.size() != 0) {
                        Log.d("LIA", admins.get(0).toString());
                        startActivity(new Intent(this, OTPActivity.class)
                                .putExtra("LOGIN", admins.get(0))
                                .putExtra("TAG", "LOGIN")
                                .putExtra("ISLOGIN", isLogin));
                    } else Toast.makeText(this,"Your username or password wrong!",
                            Toast.LENGTH_SHORT).show();
                });
                break;
            case "STAFF":
                Staffs staff = new Staffs();
                staff.setUsername(username);
                staff.setPassword(password);
                new StaffsRepository().getStaffLogin(staff).observe(this, (ArrayList<Staffs> staffs) -> {
                    if(staffs.size() != 0) {
                        startActivity(new Intent(this, OTPActivity.class)
                                .putExtra("LOGIN", staffs.get(0))
                                .putExtra("TAG", "LOGIN")
                                .putExtra("ISLOGIN", isLogin));
                    } else Toast.makeText(this,"Your username or password wrong!",
                            Toast.LENGTH_SHORT).show();
                });
                break;
        }
    }
}