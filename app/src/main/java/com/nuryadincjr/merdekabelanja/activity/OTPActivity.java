package com.nuryadincjr.merdekabelanja.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.nuryadincjr.merdekabelanja.MainActivity;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adminacitvity.AdminsActivity;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityOtpactivityBinding;
import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.usrsactivity.UsersActivity;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private ActivityOtpactivityBinding binding;
    private LocalPreference localPreference;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private Users users;
    private Admins admins;
    private Staffs staffs;
    private String action, islogin, verificationId, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        localPreference = LocalPreference.getInstance(this);

        firebaseAuth = FirebaseAuth.getInstance();
        action = getIntent().getStringExtra("TAG");
        getIsLogin();

        dialog = new ProgressDialog(this);
        onAuthentication(phone);

        binding.inputOtp.setOtpCompletionListener(this::onOtpCompleted);
    }

    private void getIsLogin() {
        if(action.equals("LOGIN")) {
            islogin = getIntent().getStringExtra("ISLOGIN");
            switch (islogin) {
                case "USER":
                    users = getIntent().getParcelableExtra("LOGIN");
                    phone = users.getPhone();
                    break;
                case "ADMIN":
                    admins = getIntent().getParcelableExtra("LOGIN");
                    phone = admins.getPhone();
                    break;
                case "STAFF":
                    staffs = getIntent().getParcelableExtra("LOGIN");
                    phone = staffs.getPhone();
                    break;
            }
        } else {
            users = getIntent().getParcelableExtra("REGISTER");
            phone = users.getPhone();
        }
    }

    private void setTimer() {
        binding.tvTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long l) {
                String onDuration = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(l));
                binding.tvTimer.setText("Please wait "+onDuration+" second for send again.");
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("Resend");
                binding.tvTimer.setOnClickListener(view -> onAuthentication(phone));
            }
        }.start();
    }

    private void onAuthentication(String phone) {
        dialog.setMessage("Sending OTP..");
        dialog.setCancelable(false);
        dialog.show();

        String labelNumber = "Verify " + phone;
        binding.txtNumber.setText(labelNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(Constaint.TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        binding.inputOtp.setText(phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        dialog.dismiss();
                        Log.w(Constaint.TAG, "onVerificationFailed", e);
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        binding.tvTimer.setText("Resend");
                        binding.tvTimer.setOnClickListener(view -> onAuthentication(phone));

                        binding.tvTimer.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        setTimer();
                        dialog.dismiss();
                        verificationId = s;

                        InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        binding.inputOtp.requestFocus();
                    }
                }).build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void onOtpCompleted(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                dialog.setMessage("Updating Profile..");
                dialog.setCancelable(false);
                dialog.show();

                Log.d(Constaint.TAG, "signInWithCredential:success");
                Toast.makeText(this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();

                if (action.equals("LOGIN")) onLogin();
                else onRegister();
            } else {
                dialog.dismiss();
                Toast.makeText(this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                Log.w(Constaint.TAG, "signInWithCredential:failure", task.getException());
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    // The verification code entered was invalid
                }
            }
        });
    }

    private void onLogin() {
        int intIsLogin = 0;

        switch (islogin) {
            case "USER":
                intIsLogin = 1;
                users = getIntent().getParcelableExtra("LOGIN");
                startActivity(new Intent(this, UsersActivity.class));
                localPreference.getEditor().putString("UID", users.getUid()).apply();
                break;
            case "ADMIN":
                intIsLogin = 2;
                admins = getIntent().getParcelableExtra("LOGIN");
                startActivity(new Intent(this, AdminsActivity.class));
                localPreference.getEditor().putString("UID", admins.getUid()).apply();
                break;
            case "STAFF":
                intIsLogin = 3;
                staffs = getIntent().getParcelableExtra("LOGIN");
                startActivity(new Intent(this, MainActivity.class));
                localPreference.getEditor().putString("UID", staffs.getUid()).apply();
                break;
        }
        localPreference.getEditor().putInt("ISLOGIN", intIsLogin).apply();
        finishAffinity();
    }

    private void onRegister() {
        users.setUid(firebaseAuth.getUid());
        users.setLatest_update(Constaint.time());
        users.setStatus_account("active");

        new UsersRepository().insertUser(users).addOnSuccessListener(documentReference -> {
            Log.d(Constaint.TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, UsersActivity.class));
            localPreference.getEditor()
                    .putString("UID", users.getUid())
                    .putInt("ISLOGIN", 1).apply();

            finishAffinity();
        }).addOnFailureListener(e -> {
            Log.w(Constaint.TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(), "Error adding document.", Toast.LENGTH_SHORT).show();
        });
        dialog.dismiss();
    }
}