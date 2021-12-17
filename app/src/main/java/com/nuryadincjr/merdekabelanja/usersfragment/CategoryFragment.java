package com.nuryadincjr.merdekabelanja.usersfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_CATEGORY;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.nuryadincjr.merdekabelanja.databinding.FragmentCategoryBinding;
import com.nuryadincjr.merdekabelanja.usrsactivity.CategoryActivity;
import com.nuryadincjr.merdekabelanja.usrsactivity.SearchActivity;

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

        binding.searchBar.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if(hasFocus) {
                startActivity(new Intent(getContext(), SearchActivity.class)
                        .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NO_ANIMATION));
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        binding.searchBar.clearFocus();
        super.onResume();
    }

    private void getOnClickListener(CardView cardView, String category) {
        cardView.setOnClickListener(v -> startActivity(new Intent(getContext(),
                CategoryActivity.class).putExtra(NAME_CATEGORY, category)));
    }

}