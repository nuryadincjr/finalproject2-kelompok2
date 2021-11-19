package com.nuryadincjr.merdekabelanja.adminfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.StaffsAdapter;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsStaffActivity;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
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

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getStaffsLiveData().observe(getViewLifecycleOwner(), staffs -> {
            List<Staffs> staffsList = new ArrayList<>(staffs);
            StaffsAdapter staffsAdapter = new StaffsAdapter(staffsList);
            binding.rvStaffs.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvStaffs.setAdapter(staffsAdapter);
            binding.rvStaffs.setItemAnimator(new DefaultItemAnimator());

            onListener(staffsAdapter, staffsList);
        });
    }

    private void onListener(StaffsAdapter staffsAdapter, List<Staffs> staffsList) {
        staffsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                startActivity(new Intent(getContext(), DetailsStaffActivity.class)
                        .putExtra("DATA", staffsList.get(position))
                        .putExtra("ISDETAIL", "ADMIN"));
            }

            @Override
            public void onLongClick(View view, int position) {
                openMenuEditPopup(view, staffsList.get(position));
            }
        });
    }

    private void getDataDelete(Staffs staffs) {
        new StaffsRepository().deleteStaffs(staffs.getUid()).addOnSuccessListener(unused -> {
            if (staffs.getPhoto() != null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storage.getReferenceFromUrl(staffs.getPhoto()).delete();
            }
        });
        getData();
    }

    public void openMenuEditPopup(View view, Staffs staffs) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.act_saves).setVisible(false);
        menu.getMenu().findItem(R.id.act_cencle).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.act_edit:
                    startActivity(new Intent(getContext(), DetailsStaffActivity.class)
                            .putExtra("DATA", staffs)
                            .putExtra("ISEDIT", true));
                    break;
                case R.id.act_delete:
                    getDataDelete(staffs);
                    break;
                case R.id.act_print:

                    break;
            }
            return true;
        });
        menu.show();
    }
}