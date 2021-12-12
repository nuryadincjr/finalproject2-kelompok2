package com.nuryadincjr.merdekabelanja.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.databinding.ListItemHadlineBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Headline;

import java.util.List;

public class HeadlineAdapter extends RecyclerView.Adapter<HeadlineAdapter.HeadlineViewHolder> {

    public List<Headline> data;
    public ViewPager2 viewPager2;
    public ItemClickListener itemClickListener;

    public HeadlineAdapter(List<Headline> data, ViewPager2 viewPager2) {
        this.data = data;
        this.viewPager2 = viewPager2;
    }

    @NonNull
    @Override
    public HeadlineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemHadlineBinding binding = ListItemHadlineBinding
                .inflate(LayoutInflater.from(parent.getContext()),
                        parent, false);

        return new HeadlineViewHolder(this, binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HeadlineViewHolder holder, int position) {
        holder.setDataToView(data.get(position));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public class HeadlineViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        private final HeadlineAdapter usersAdapter;
        private final ListItemHadlineBinding binding;

        public HeadlineViewHolder(HeadlineAdapter usersAdapter, ListItemHadlineBinding binding) {
            super(binding.getRoot());
            this.usersAdapter = usersAdapter;
            this.binding = binding;

            binding.getRoot().setOnLongClickListener(this);
            binding.getRoot().setOnClickListener(this);
        }

        public void setDataToView(Headline headline) {
            Glide.with(itemView.getContext())
                    .load(headline.getImage())
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.imageView2);
            binding.tvHeadline.setText(headline.getHeadline());
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

