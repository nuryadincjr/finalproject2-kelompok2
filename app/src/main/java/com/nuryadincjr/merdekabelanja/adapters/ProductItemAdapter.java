package com.nuryadincjr.merdekabelanja.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetalisProductBinding;
import com.nuryadincjr.merdekabelanja.databinding.ListItemProductBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;

import java.util.List;
import java.util.Map;

public class ProductItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public Object[] key;
    public Map<String, Object> value ;
    public ItemClickListener itemClickListener;
    public final int sesssion;
    public final int SECTION_VIEW = 0;
    public final int CONTENT_VIEW = 1;
    public ActivityDetalisProductBinding productBinding;

    public ProductItemAdapter(Object[] key, Map<String, Object> value,
                              int sesssion, ActivityDetalisProductBinding productBinding) {
        this.key = key;
        this.value = value;
        this.sesssion = sesssion;
        this.productBinding = productBinding;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemProductBinding binding = ListItemProductBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);

        if (viewType == SECTION_VIEW) {
            return new ProductItemsViewHolder(this, binding);
        } else {
            return new ProductInfoViewHolder(this, binding);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (sesssion == 0) {
            return SECTION_VIEW;
        } else {
            return CONTENT_VIEW;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (SECTION_VIEW == getItemViewType(position)) {
            ProductItemsViewHolder productItemsViewHolder = (ProductItemsViewHolder) holder;
            productItemsViewHolder.setDataToView(key[position].toString(), value);
        } else {
            ProductInfoViewHolder productInfoViewHolder = (ProductInfoViewHolder) holder;
            productInfoViewHolder.setDataToView(key[position].toString(), value);
        }
    }

    @Override
    public int getItemCount() {
        return key.length;
    }

    public class ProductItemsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private final ProductItemAdapter productItemAdapter;
        private final ListItemProductBinding binding;

        public ProductItemsViewHolder(ProductItemAdapter productItemAdapter, ListItemProductBinding binding) {
            super(binding.getRoot());
            this.productItemAdapter = productItemAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        public void setDataToView(String key, Map<String, Object> value) {
            String newValue = value.get(key).toString()
                    .replace("[", "")
                    .replace("]", "");
            if(!key.equals("photo")){
                String fildName = key.substring(0,1).toUpperCase() + key.substring(1).toLowerCase();
                binding.tvItem.setText(newValue);
                binding.tvLable.setText(fildName.replace("_", " "));
            } else {
                binding.tvItem.setVisibility(View.GONE);
                binding.tvLable.setVisibility(View.GONE);
                ImagesPreference imagesPreference = new ImagesPreference(itemView.getContext());
                List<String> urlList = imagesPreference.getList(newValue);

                if(urlList.size()!=0) {
                    for(int i=0; i<urlList.size(); i++) {
                        RequestBuilder<Drawable> glide = Glide.with(itemView.getContext())
                                .load(urlList.get(i))
                                .centerCrop()
                                .placeholder(R.drawable.ic_brand);

                        switch (i) {
                            case 0:
                                glide.into(productBinding.imageView1);
                                break;
                            case 1:
                                glide.into(productBinding.imageView2);
                                break;
                            case 2:
                                glide.into(productBinding.imageView3);
                                break;
                        }
                    }
                }
            }
        }

        @Override
        public void onClick(View view) {
            if (productItemAdapter.itemClickListener != null)
                productItemAdapter.itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (productItemAdapter.itemClickListener != null)
                productItemAdapter.itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }

    public class ProductInfoViewHolder extends RecyclerView.ViewHolder {
        private final ProductItemAdapter productItemAdapter;
        private final ListItemProductBinding binding;

        public ProductInfoViewHolder(ProductItemAdapter productItemAdapter, ListItemProductBinding binding) {
            super(binding.getRoot());
            this.productItemAdapter = productItemAdapter;
            this.binding = binding;
        }

        public void setDataToView(String key, Map<String, Object> value) {
            String newValue = value.get(key).toString()
                    .replace("[", "").
                    replace("]", "");

            if(key.equals("photo") || key.equals("descriptions") ||
                    key.equals("id") || key.equals("latest_update")){
                binding.tvItem.setVisibility(View.GONE);
                binding.tvLable.setVisibility(View.GONE);
            } else {
                String fildName = key.substring(0,1).toUpperCase() + key.substring(1).toLowerCase();
                binding.tvItem.setText(newValue);
                binding.tvLable.setText(fildName.replace("_", " "));
            }
        }
    }
}
