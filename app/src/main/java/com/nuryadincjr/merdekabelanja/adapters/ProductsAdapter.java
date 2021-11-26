package com.nuryadincjr.merdekabelanja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuryadincjr.merdekabelanja.databinding.ListItemBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {

    public List<Products> data;
    public ItemClickListener itemClickListener;

    public ProductsAdapter(List<Products> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);

        return new ProductsViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsViewHolder holder, int position) {
        holder.setDataToView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class ProductsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final ProductsAdapter productsAdapter;
        private final ListItemBinding binding;

        public ProductsViewHolder(ProductsAdapter productsAdapter, ListItemBinding binding) {
            super(binding.getRoot());
            this.productsAdapter = productsAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        public void setDataToView(Products products) {
            binding.tvName.setText(products.getName());
        }

        @Override
        public void onClick(View view) {
            if (productsAdapter.itemClickListener != null)
                productsAdapter.itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (productsAdapter.itemClickListener != null)
                productsAdapter.itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}

