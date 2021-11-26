package com.nuryadincjr.merdekabelanja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ActivityDetalisProductBinding;
import com.nuryadincjr.merdekabelanja.databinding.ListLableBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.pojo.ImagesPreference;

import java.util.List;
import java.util.Map;

public class ItemLableAdapter extends RecyclerView.Adapter<ItemLableAdapter.ItemLableViewHolder> {

    public Object[] key;
    public Map<String, Object> value ;
    public ItemClickListener itemClickListener;
    private final ActivityDetalisProductBinding productBinding;

    public ItemLableAdapter(Object[] key, Map<String, Object> value,
                            ActivityDetalisProductBinding productBinding) {
        this.key = key;
        this.value = value;
        this.productBinding = productBinding;
    }

    @NonNull
    @Override
    public ItemLableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListLableBinding binding = ListLableBinding
                .inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);

        return new ItemLableViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemLableViewHolder holder, int position) {
        holder.setDataToView(key[position].toString(), value);
    }

    @Override
    public int getItemCount() {
        return key.length;
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ItemLableViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener{

        private final ItemLableAdapter itemLableAdapter;
        private final ListLableBinding binding;

        public ItemLableViewHolder(ItemLableAdapter itemLableAdapter, ListLableBinding binding) {
            super(binding.getRoot());
            this.itemLableAdapter = itemLableAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        public void setDataToView(String key, Map<String, Object> value ) {
            String newValue = value.get(key).toString()
                    .replace("[", "")
                    .replace("]", "");
            if(!key.equals("photo")){
                binding.tvItem.setText(newValue);
                binding.tvLable.setText(key.toUpperCase().replace("_", " "));
            } else {
                binding.tvItem.setVisibility(View.GONE);
                binding.tvLable.setVisibility(View.GONE);
                ImagesPreference imagesPreference = new ImagesPreference(itemView.getContext());
                List<String> urlList = imagesPreference.getList(newValue);

                Glide.with(itemView.getContext())
                        .load(urlList.get(0))
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(itemLableAdapter.productBinding.ivPhoto);
            }
        }

        @Override
        public void onClick(View view) {
            if (itemLableAdapter.itemClickListener != null)
                itemLableAdapter.itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (itemLableAdapter.itemClickListener != null)
                itemLableAdapter.itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}

