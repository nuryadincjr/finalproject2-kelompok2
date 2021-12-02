package com.nuryadincjr.merdekabelanja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ListItemBinding;
import com.nuryadincjr.merdekabelanja.databinding.ListProductBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Products;

import java.util.List;

public class ProductsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<Products> data;
    public ItemClickListener itemClickListener;
    public final int SECTION_VIEW = 0;
    public final int CONTENT_VIEW = 1;
    public final int sesssion;


    public ProductsAdapter(int sesssion, List<Products> data) {
        this.data = data;
        this.sesssion = sesssion;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SECTION_VIEW) {
            ListItemBinding binding = ListItemBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new ProductsViewHolder(this, binding);
        } else {
            ListProductBinding binding = ListProductBinding
                    .inflate(LayoutInflater.from(parent.getContext()), parent, false);

            return new OtherProductsViewHolder(this, binding);
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
            ProductsViewHolder productsViewHolder = (ProductsViewHolder) holder;
            productsViewHolder.setDataToView(data.get(position));
        } else {
            OtherProductsViewHolder otherProductsViewHolder = (OtherProductsViewHolder) holder;
            otherProductsViewHolder.setDataToView(data.get(position));
        }
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

    public class OtherProductsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final ProductsAdapter productsAdapter;
        private final ListProductBinding binding;

        public OtherProductsViewHolder(ProductsAdapter productsAdapter, ListProductBinding binding) {
            super(binding.getRoot());
            this.productsAdapter = productsAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        public void setDataToView(Products products) {
            binding.tvTitle.setText(products.getName());
            binding.tvPiece.setText("IDR "+products.getPiece() );
            binding.tvStock.setText(products.getQuantity() + " PIC");

            if(!products.getPhoto().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(products.getPhoto().get(0))
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);
            }
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

