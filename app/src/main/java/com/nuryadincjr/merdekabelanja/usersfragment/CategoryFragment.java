package com.nuryadincjr.merdekabelanja.usersfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.nuryadincjr.merdekabelanja.databinding.FragmentCategoryBinding;
import com.nuryadincjr.merdekabelanja.usrsactivity.CategoryActivity;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding binding;

    public CategoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCategoryBinding.inflate(inflater, container, false);

        getOnClickListener(binding.cvClothing, "Clothing");
        getOnClickListener(binding.cvElectronics, "Electronic");
        getOnClickListener(binding.cvBooks, "Book");
        getOnClickListener(binding.cvOTher, "Other Products");

        return binding.getRoot();
    }

    private void getOnClickListener(CardView cardView, String category) {
        cardView.setOnClickListener(v -> startActivity(new Intent(getContext(),
                CategoryActivity.class).putExtra("ISCATEGORY", category)));
    }
}