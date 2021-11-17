package com.nuryadincjr.merdekabelanja;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.models.Users;

public class MainActivity extends AppCompatActivity {
    private Users users;
    private Admins admins;
    private Staffs staffs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String islogin = getIntent().getStringExtra("ISLOGIN");
        switch (islogin) {
            case "USER":
                users = getIntent().getParcelableExtra("USERS");
                Toast.makeText(this, users.getName(), Toast.LENGTH_SHORT).show();
                break;
            case "ADMIN":
                admins = getIntent().getParcelableExtra("USERS");
                Toast.makeText(this, admins.getName(), Toast.LENGTH_SHORT).show();
                break;
            case "STAFF":
                staffs = getIntent().getParcelableExtra("USERS");
                Toast.makeText(this, staffs.getName(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}