package com.nuryadincjr.merdekabelanja.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ListItemBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Users;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersViewHolder> {

    public List<Users> data;
    public ItemClickListener itemClickListener;

    public UsersAdapter(List<Users> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);

        return new UsersViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        holder.setDataToView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final UsersAdapter usersAdapter;
        private final ListItemBinding binding;

        public UsersViewHolder(UsersAdapter usersAdapter, ListItemBinding binding) {
            super(binding.getRoot());
            this.usersAdapter = usersAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setDataToView(Users users) {
            binding.tvLabelOption.setText("Name");
            binding.tvName.setText(users.getName());
            binding.tvLabelOptionSecond.setText("Phone Number");
            binding.tvNameSecond.setText(users.getPhone());
            if(!users.getPhoto().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(users.getPhoto())
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);
            }
        }


        @Override
        public void onClick(View view) {
            if (usersAdapter.itemClickListener != null)
                usersAdapter.itemClickListener.onClick(view, getAdapterPosition());
        }

        @Override
        public boolean onLongClick(View view) {
            if (usersAdapter.itemClickListener != null)
                usersAdapter.itemClickListener.onLongClick(view, getAdapterPosition());
            return true;
        }
    }
}

