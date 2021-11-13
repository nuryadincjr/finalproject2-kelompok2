package com.nuryadincjr.merdekabelanja.activity;

import android.app.Activity;
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
import com.nuryadincjr.merdekabelanja.activity.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityOtpactivityBinding;
import com.nuryadincjr.merdekabelanja.pojo.Users;

import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {

    private ActivityOtpactivityBinding binding;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog dialog;
    private Users users;
    private String verificationId;
    private static final String TAG = Activity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpactivity);

        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        users = getIntent().getParcelableExtra("REGISTER");

        dialog = new ProgressDialog(this);
        onAuthentication(users);

    }

    private void setTimer() {
        binding.tvTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(120000, 1000) {
            @Override
            public void onTick(long l) {
                String onDuration = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(l));
                binding.tvTimer.setText("Please white "+onDuration+" second for send again.");
            }

            @Override
            public void onFinish() {
                binding.tvTimer.setText("Resend");
                binding.tvTimer.setOnClickListener(view -> onAuthentication(users));
            }
        }.start();
    }

    private void onAuthentication(Users users) {
        dialog.setMessage("Sending OTP..");
        dialog.setCancelable(false);
        dialog.show();

        String labelNumber = "Verify " + users.getPhone();
        binding.txtNumber.setText(labelNumber);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(users.getPhone())
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        binding.inputOtp.setText(phoneAuthCredential.getSmsCode());
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        dialog.dismiss();
                        Log.w(TAG, "onVerificationFailed", e);
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                        binding.tvTimer.setText("Resend");
                        binding.tvTimer.setOnClickListener(view -> onAuthentication(users));

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

        binding.inputOtp.setOtpCompletionListener(otp -> {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);

            firebaseAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    dialog.setMessage("Updating Profile..");
                    dialog.setCancelable(false);
                    dialog.show();

                    Log.d(TAG, "signInWithCredential:success");
                    Toast.makeText(this, "signInWithCredential:success", Toast.LENGTH_SHORT).show();
                    onRegister();
                } else {
                    dialog.dismiss();
                    Toast.makeText(this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                        // The verification code entered was invalid
                    }
                }
            });
        });
    }

    private void onRegister() {
        users.setUid(firebaseAuth.getUid());

        new UsersRepository().insertUser(users).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
            Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, MainActivity.class));
            finishAffinity();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(), "Error adding document.", Toast.LENGTH_SHORT).show();
        });
        dialog.dismiss();
    }
}