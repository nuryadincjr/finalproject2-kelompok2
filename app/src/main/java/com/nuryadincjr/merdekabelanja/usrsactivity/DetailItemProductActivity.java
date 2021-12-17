package com.nuryadincjr.merdekabelanja.usrsactivity;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.SESSION_FIRST;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.ProductItemAdapter;
import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetailItemProductBinding;
import com.nuryadincjr.merdekabelanja.models.Products;

import java.util.Map;

public class DetailItemProductActivity extends AppCompatActivity {

    private ActivityDetailItemProductBinding binding;
    private Products data;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_item_product);
        binding = ActivityDetailItemProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data = getIntent().getParcelableExtra(NAME_DATA);
    }

    @Override
    protected void onResume() {
        onDataSet(data);
        super.onResume();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("SetTextI18n")
    private void onDataSet(Products product) {
        binding.tvName.setText(product.getName());
        binding.tvPiece.setText("IDR "+product.getPiece());
        binding.tvDescriptions.setText(product.getDescriptions());
        binding.tvStock.setText("Available "+product.getQuantity()+" PIC");
        binding.tvCategory.setText(product.getCategory());

        if(product.getPhoto().size()!=0) {
            for(int i=0; i<product.getPhoto().size(); i++) {
                RequestBuilder<Drawable> glide = Glide.with(this)
                        .load(product.getPhoto().get(i))
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand);

                switch (i) {
                    case 0:
                        glide.into(binding.imageView1);
                        break;
                    case 1:
                        glide.into(binding.imageView2);
                        break;
                    case 2:
                        glide.into(binding.imageView3);
                        break;
                }
            }
        }
        new ProductsRepository().getSinggleProduct(product).
                observe(this, (Map<String, Object> maps) -> {
                    Object[] key = maps.keySet().toArray();

                    ProductItemAdapter productItemAdapter =
                            new ProductItemAdapter(key, maps, SESSION_FIRST, null);
                    binding.rvLable.setLayoutManager(new LinearLayoutManager(this));
                    binding.rvLable.setAdapter(productItemAdapter);
                    binding.rvLable.setItemAnimator(new DefaultItemAnimator());
        });
    }
}