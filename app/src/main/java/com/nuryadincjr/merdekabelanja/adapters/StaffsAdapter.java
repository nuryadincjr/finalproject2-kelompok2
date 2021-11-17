package com.nuryadincjr.merdekabelanja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nuryadincjr.merdekabelanja.databinding.ListItemBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Staffs;

import java.util.List;

public class StaffsAdapter extends RecyclerView.Adapter<StaffsAdapter.StaffsViewHolder> {

    public List<Staffs> data;
    public ItemClickListener itemClickListener;

    public StaffsAdapter(List<Staffs> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public StaffsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = ListItemBinding
                .inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);

        return new StaffsViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StaffsViewHolder holder, int position) {
        holder.setDataToView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class StaffsViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final StaffsAdapter staffsAdapter;
        private final ListItemBinding binding;

        public StaffsViewHolder(StaffsAdapter staffsAdapter, ListItemBinding binding) {
            super(binding.getRoot());
            this.staffsAdapter = staffsAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        public void setDataToView(Staffs staffs) {
            binding.tvName.setText(staffs.getName());
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

