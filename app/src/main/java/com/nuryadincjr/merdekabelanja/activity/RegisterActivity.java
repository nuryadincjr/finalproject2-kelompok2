package com.nuryadincjr.merdekabelanja.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityRegisterBinding;
import com.nuryadincjr.merdekabelanja.pojo.Users;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> getRegister());
    }

    private void getRegister() {

        String fullname = binding.etName.getText().toString();
        String phone = binding.etPhone.getText().toString();
        String email = binding.etEmail.getText().toString();
        String password = binding.etPassword.getText().toString();
        String confpassword = binding.etConfPassword.getText().toString();

       if(!fullname.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
               !password.isEmpty() && !confpassword.isEmpty()) {
           if(password.length() > 7) {
               if(password.equals(confpassword)){
                   Users users = new  Users("", fullname, phone, email, "", email, password);
                   startActivity(new Intent(this, OTPActivity.class)
                           .putExtra("REGISTER", users));
               } else binding.etPassword.setError("Password canot equals!");
           } else binding.etConfPassword.setError("Password too short!");
       } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }


}