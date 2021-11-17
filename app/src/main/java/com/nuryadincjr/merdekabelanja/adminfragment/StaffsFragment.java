package com.nuryadincjr.merdekabelanja.adminfragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.StaffsAdapter;
import com.nuryadincjr.merdekabelanja.databinding.FragmentStaffsBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class StaffsFragment extends Fragment {

    private FragmentStaffsBinding binding;
    private MainViewModel mainViewModel;

    public StaffsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStaffsBinding.inflate(inflater, container, false);

        binding.swipeRefresh.setColorSchemeResources(R.color.black);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getData();
            binding.swipeRefresh.setRefreshing(false);
        });

        getData();
        return binding.getRoot();
    }

    private void getData() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getStaffsLiveData().observe(getViewLifecycleOwner(), staffs -> {
            List<Staffs> staffsList = new ArrayList<>(staffs);
            StaffsAdapter staffsAdapter = new StaffsAdapter(staffsList);
            binding.rvStaffs.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvStaffs.setAdapter(staffsAdapter);
            binding.rvStaffs.setItemAnimator(new DefaultItemAnimator());

            onListener(staffsAdapter);
        });
    }

    private void onListener(StaffsAdapter staffsAdapter) {
        staffsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Toast.makeText(getContext(), "OnClick", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {
                Toast.makeText(getContext(), "OnLongClick", Toast.LENGTH_SHORT).show();
            }
        });
    }
}