package com.nuryadincjr.merdekabelanja.activity;


import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ACTION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_LOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_REGISTER;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.hbb20.CountryCodePicker;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityRegisterBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.ccp.registerCarrierNumberEditText(binding.etPhone);
        binding.ccp.setCountryForNameCode("ID");
        binding.btnRegister.setOnClickListener(v -> getInputValidations());
        binding.tvLoginUser.setOnClickListener(v ->
                startActivity(new Intent(this, LoginActivity.class)
                        .putExtra(NAME_LOGIN, "USER")));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void getInputValidations() {
        String fullName = String.valueOf(binding.etName.getText());
        String phone = binding.ccp.getFullNumberWithPlus();
        String email = String.valueOf(binding.etEmail.getText());
        String password = String.valueOf(binding.etPassword.getText());
        String confpassword = String.valueOf(binding.etConfPassword.getText());
        boolean isPhoneCurtly = binding.ccp.isValidFullNumber();

        if(!fullName.isEmpty() && !phone.isEmpty() && !email.isEmpty() &&
               !password.isEmpty() && !confpassword.isEmpty()) {
           if(password.length() > 7) {
               if(password.equals(confpassword)){
                   if(isPhoneCurtly){
                       MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
                       mainViewModel.getUserPhone(phone).observe(this, user -> {
                           if(user.size() ==0){
                               Users users = new  Users("", fullName, phone, email,
                                       "", "", email, password,
                                       "" ,"register","");
                               onRegisters(users);
                           }else binding.etPhone.setError("This phone number is already exists, Login now!");
                       });
                   } else binding.etPhone.setError("Phone number cannot found!");
               } else binding.etPassword.setError("Password cannot equals!");
           } else binding.etConfPassword.setError("Password too short!");
       } else Toast.makeText(this,"Empty credentials!", Toast.LENGTH_SHORT).show();
    }

    private void onRegisters(Users users) {
        startActivity(new Intent(this, OTPActivity.class)
                .putExtra(NAME_REGISTER, users)
                .putExtra(NAME_ACTION, "REGISTER"));
    }
}