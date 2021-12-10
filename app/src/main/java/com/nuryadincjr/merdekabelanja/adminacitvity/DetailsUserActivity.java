package com.nuryadincjr.merdekabelanja.adminacitvity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetailsUserBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.PdfConverters;

import java.util.ArrayList;

public class DetailsUserActivity extends AppCompatActivity {
    private ActivityDetailsUserBinding binding;
    private FirebaseStorage storage;
    private Users data;
    private boolean isPrint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_user);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Details Users");

        binding = ActivityDetailsUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        data = getIntent().getParcelableExtra("DATA");
        isPrint = getIntent().getBooleanExtra("ISPRINT", false);
    }

    @Override
    protected void onResume() {
        onDataSet(data);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        menu.findItem(R.id.itemSaves).setVisible(false);
        menu.findItem(R.id.itemCencle).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.itemEdit:
                getDataEdited();
                return true;
            case R.id.itemDelete:
                getDataDelete();
                return true;
            case R.id.itemPrint:
                getIsPrint();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getDataDelete() {
        new UsersRepository().deleteUser(data.getUid()).addOnSuccessListener(unused -> {
            if (data.getPhoto() != null){
                storage.getReferenceFromUrl(data.getPhoto()).delete();
            }
        });
        finish();
    }

    private void getDataEdited() {
//        startActivity(new Intent(getContext(), RegisterActivity.class)
//                            .putExtra("DATA", users)
//                            .putExtra("ISEDITED", true));
    }

    private void onDataSet(Users users) {
        new UsersRepository().getUserData(users.getUid()).observe(this, (ArrayList<Users> user) -> {
            if(user.size() != 0) {
                data = user.get(0);

                String url = user.get(0).getPhoto();
                Glide.with(this)
                        .load(url)
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);

                binding.tvId.setText(user.get(0).getUid());
                binding.tvName.setText(user.get(0).getName());
                binding.tvPhone.setText(user.get(0).getPhone());
                binding.tvEmail.setText(user.get(0).getEmail());
                binding.tvAddress.setText(user.get(0).getAddress());
                binding.tvAddress2.setText(user.get(0).getAddress2());
                binding.tvUsername.setText(user.get(0).getUsername());
                binding.tvAccount.setText(user.get(0).getStatus_account());
                binding.tvLatestUpdate.setText(user.get(0).getLatest_update());

                if (isPrint) getIsPrint();
            }
        });
    }

    private void getIsPrint() {
        if(binding.getRoot().getWidth() != 0 &&
                binding.getRoot().getHeight() !=0){
            PdfConverters.getInstance(this)
                    .getDataToPdf(binding.getRoot(), data.getUid());
        }
    }
}