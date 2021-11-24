package com.nuryadincjr.merdekabelanja.adminfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.nuryadincjr.merdekabelanja.databinding.FragmentStocksBinding;

public class StocksFragment extends Fragment {
    private FragmentStocksBinding binding;

    public StocksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStocksBinding.inflate(inflater, container, false);
        getListenerProduce(binding.llClothing, "colething");
        getListenerProduce(binding.llBooks, "book");
        getListenerProduce(binding.llElectronic, "electronic");
        getListenerProduce(binding.llOther, "other");

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Products");
        super.onResume();
    }

    private void getListenerProduce(LinearLayout layout, String produce) {
        layout.setOnClickListener(v -> Toast.makeText(getContext(), produce, Toast.LENGTH_SHORT).show());
    }

}