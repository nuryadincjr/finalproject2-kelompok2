package com.nuryadincjr.merdekabelanja.adminfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.nuryadincjr.merdekabelanja.adminacitvity.AddStafsActivity;
import com.nuryadincjr.merdekabelanja.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        binding.llAddStaff.setOnClickListener(v -> startActivity(new Intent(getContext(), AddStafsActivity.class)));

//        binding.llAddStock.setOnClickListener(v -> startActivity(new Intent(getContext(), AddStocksActivity.class)));

        return binding.getRoot();
    }
}