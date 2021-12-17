package com.nuryadincjr.merdekabelanja.activity;


import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ACTION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_REGISTER;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityRegisterBinding;
import com.nuryadincjr.merdekabelanja.models.Users;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnRegister.setOnClickListener(v -> getInputValidations());
    }

    private void getInputValidations() {
        String fullName = String.valueOf(binding.etName.getText());
        String phone = String.valueOf(binding.etPhone.getText());
        String email = String.valueOf(binding.etEmail.getText());
        String password = String.valueOf(binding.etPassword.getText());
        String confpassword = String.valueOf(binding.etConfPassword.getText());

       if(!fullName.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
               !password.isEmpty() && !confpassword.isEmpty()) {
           if(password.length() > 7) {
               if(password.equals(confpassword)){
                   Users users = new  Users("", fullName, phone, email,
                           "", "", email, password, "" ,"register","");
                   onRegisters(users);
               } else binding.etPassword.setError("Password canot equals!");
           } else binding.etConfPassword.setError("Password too short!");
       } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onRegisters(Users users) {
        startActivity(new Intent(this, OTPActivity.class)
                .putExtra(NAME_REGISTER, users)
                .putExtra(NAME_ACTION, "REGISTER"));
    }
}