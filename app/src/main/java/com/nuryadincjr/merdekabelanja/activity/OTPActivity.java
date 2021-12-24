package com.nuryadincjr.merdekabelanja.activity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_ISLOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ACTION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_EDITED;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISLOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_LOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_REGISTER;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentValues;
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
import com.google.firebase.auth.FirebaseAuth;
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
        action = getIntent().getStringExtra(NAME_ACTION);
        dialog = new ProgressDialog(this);

        getLogin();
        onAuthentication(phone);

        binding.inputOtp.setOtpCompletionListener(this::onOtpCompleted);
    }

    private void getLogin() {
        if(action.equals("LOGIN")) {
            islogin = getIntent().getStringExtra(NAME_ISLOGIN);
            switch (islogin) {
                case "USER":
                    users = getIntent().getParcelableExtra(NAME_LOGIN);
                    phone = users.getPhone();
                    break;
                case "ADMIN":
                    admins = getIntent().getParcelableExtra(NAME_LOGIN);
                    phone = admins.getPhone();
                    break;
                case "STAFF":
                    staffs = getIntent().getParcelableExtra(NAME_LOGIN);
                    phone = staffs.getPhone();
                    break;
            }
        } else if(action.equals("SECURITY")) {
            users = getIntent().getParcelableExtra(NAME_EDITED);
            phone = users.getPhone();
        } else {
            users = getIntent().getParcelableExtra(NAME_REGISTER);
            phone = users.getPhone();
        }
    }

    private void setTimer() {
        binding.tvTimer.setVisibility(View.VISIBLE);
        new CountDownTimer(120000, 1000) {
            @SuppressLint({"DefaultLocale", "SetTextI18n"})
            @Override
            public void onTick(long l) {
                String onDuration = String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(l));
                binding.tvTimer.setText("Please wait "+onDuration+" second for send again.");
            }

            @SuppressLint("SetTextI18n")
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
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);
                        binding.inputOtp.setText(phoneAuthCredential.getSmsCode());
                    }

                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        dialog.dismiss();
                        Log.w(TAG, "onVerificationFailed", e);
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();

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

                if (action.equals("LOGIN")) onLogin();
                else if(action.equals("SECURITY")) onDataEdited();
                else onRegister();

                Log.d(TAG, "signInWithCredential:success");
            } else {
                dialog.dismiss();
                Toast.makeText(this, "signInWithCredential:failure", Toast.LENGTH_SHORT).show();
                Log.w(TAG, "signInWithCredential:failure", task.getException());
            }
        });
    }

    private void onLogin() {
        switch (islogin) {
            case "USER":
                users = getIntent().getParcelableExtra(NAME_LOGIN);
                getLogin(UsersActivity.class, users.getUid(), 1);
                break;
            case "ADMIN":
                admins = getIntent().getParcelableExtra(NAME_LOGIN);
                getLogin(AdminsActivity.class, admins.getUid(), 2);
                break;
            case "STAFF":
                staffs = getIntent().getParcelableExtra(NAME_LOGIN);
                getLogin(MainActivity.class, staffs.getUid(), 3);
                break;
        }
    }

    private <T> void getLogin(Class<T> tClass, String value, int intIsLogin) {
        localPreference.getEditor()
                .putString(KEY_UID, value)
                .putInt(KEY_ISLOGIN, intIsLogin).apply();
        startActivity(new Intent(this, tClass));
        finishAffinity();
    }

    private void onRegister() {
        users.setUid(firebaseAuth.getUid());
        users.setLatest_update(time());
        users.setStatus_account("active");

        new UsersRepository().insertUser(users).addOnSuccessListener(documentReference -> {
            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_SHORT).show();

            localPreference.getEditor()
                    .putString(KEY_UID, users.getUid())
                    .putInt(KEY_ISLOGIN, 1).apply();

            startActivity(new Intent(this, UsersActivity.class));
            finishAffinity();
        }).addOnFailureListener(e -> {
            Log.w(TAG, "Error adding document", e);
            Toast.makeText(getApplicationContext(), "Error adding document.", Toast.LENGTH_SHORT).show();
        });
        dialog.dismiss();
    }

    private void onDataEdited() {
        dialog.setMessage("Updating account");
        dialog.show();
        new UsersRepository().updateUser(users).addOnSuccessListener(documentReference -> {
            Log.d(ContentValues.TAG, "DocumentSnapshot added with ID: " + documentReference);
            Toast.makeText(this,"Success.", Toast.LENGTH_SHORT).show();

            dialog.dismiss();
            finish();
        }).addOnFailureListener(e -> {
            Log.w(ContentValues.TAG, "Error adding document", e);
            Toast.makeText(this, "Error adding document.", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
            finish();
        });
    }
}