package com.nuryadincjr.merdekabelanja.adapters;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ListImageViewerBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;

import java.util.List;

public class ImageViewerAdapter extends RecyclerView.Adapter<ImageViewerAdapter.ImageViewerViewHolder> {

    public List<Uri> data;
    public ItemClickListener itemClickListener;

    public ImageViewerAdapter(List<Uri> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ImageViewerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListImageViewerBinding binding = ListImageViewerBinding
                .inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);

        return new ImageViewerViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewerViewHolder holder, int position) {
        holder.setDataToView(String.valueOf(data.get(position)));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class ImageViewerViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final ImageViewerAdapter staffsAdapter;
        private final ListImageViewerBinding binding;

        public ImageViewerViewHolder(ImageViewerAdapter staffsAdapter, ListImageViewerBinding binding) {
            super(binding.getRoot());
            this.staffsAdapter = staffsAdapter;
            this.binding = binding;

            binding.ibRemove.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setDataToView(String item) {
            Glide.with(itemView.getContext())
                    .load(item)
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);

        }

        @Override
        public void onClick(View view) {
            if (staffsAdapter.itemClickListener != null)
                staffsAdapter.itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (staffsAdapter.itemClickListener != null)
                staffsAdapter.itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}

