package com.nuryadincjr.merdekabelanja.adminfragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.StaffsAdapter;
import com.nuryadincjr.merdekabelanja.adminacitvity.AddStafsActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsStaffActivity;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.databinding.FragmentStaffsBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StaffsFragment extends Fragment {

    private FragmentStaffsBinding binding;
    private LocalPreference localPreference;
    private MainViewModel mainViewModel;
    private Set<String> fliterStaffs;
    private String[] collect;
    private Menu menu;

    public StaffsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStaffsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Stafs");

        collect = getResources().getStringArray(R.array.devision);
        localPreference = LocalPreference.getInstance(getContext());
        fliterStaffs = localPreference.getPreferences()
                .getStringSet("FLITER_DEVISION", new HashSet<>(Arrays.asList(collect)));

        binding.swipeRefresh.setColorSchemeResources(R.color.black);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getData();
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.rvStaffs.addOnScrollListener(getScrollListener());
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddStafsActivity.class)));

        if(savedInstanceState == null) {
            getData();
        }

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        getData();
        super.onResume();
    }

    @NonNull
    private RecyclerView.OnScrollListener getScrollListener() {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !binding.fabAdd.isShown())
                    binding.fabAdd.show();
                else if (dy > 0 && binding.fabAdd.isShown())
                    binding.fabAdd.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category_devisions, menu);
        this.menu = menu;

        if(fliterStaffs.size() == 0 || fliterStaffs.size() == 4){
            isSetFilters(true,  true, false);
            fliterStaffs.addAll(Arrays.asList(collect));
            getData();
        } else {
            for (String filter : fliterStaffs) {
                if (filter.equals(collect[0])) menu.findItem(R.id.itemFilter1).setChecked(true);
                if (filter.equals(collect[1])) menu.findItem(R.id.itemFilter2).setChecked(true);
                if (filter.equals(collect[2])) menu.findItem(R.id.itemFilter3).setChecked(true);
                if (filter.equals(collect[3])) menu.findItem(R.id.itemFilter4).setChecked(true);
            }
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        item.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.itemFilter0:
                        if(item.isChecked()) fliterStaffs.removeAll(Arrays.asList(collect));
                        else fliterStaffs.addAll(Arrays.asList(collect));

                        isSetFilters(!item.isChecked(), !item.isChecked(), item.isChecked());
                        break;
                    case R.id.itemFilter1:
                        if(item.isChecked()) fliterStaffs.remove("Finance");
                        else fliterStaffs.add("Finance");

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter2:
                        if(item.isChecked()) fliterStaffs.remove("Marketing");
                        else fliterStaffs.add("Marketing");

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter3:
                        if(item.isChecked()) fliterStaffs.remove("Warehouse");
                        else fliterStaffs.add("Warehouse");

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter4:
                        if(item.isChecked()) fliterStaffs.remove("HRD");
                        else fliterStaffs.add("HRD");

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                }
                localPreference.getEditor().putStringSet("FLITER_DEVISION", fliterStaffs).apply();
                getData();
                return false;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                return false;
            }
        });

        return super.onOptionsItemSelected(item);
    }

    private void isSetFilters(boolean bAll, boolean bChecked, boolean bEnable) {
        menu.findItem(R.id.itemFilter0).setChecked(bAll);
        menu.findItem(R.id.itemFilter1).setChecked(bChecked).setEnabled(bEnable);
        menu.findItem(R.id.itemFilter2).setChecked(bChecked).setEnabled(bEnable);
        menu.findItem(R.id.itemFilter3).setChecked(bChecked).setEnabled(bEnable);
        menu.findItem(R.id.itemFilter4).setChecked(bChecked).setEnabled(bEnable);
    }

    private void getData() {
        if(fliterStaffs.size() !=0) {
            String[] valueList = fliterStaffs.toArray(new String[fliterStaffs.size()]);

            mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
            mainViewModel.getFilterStaffsLiveData(valueList).observe(getViewLifecycleOwner(), staffs -> {
                List<Staffs> staffsList = new ArrayList<>(staffs);
                StaffsAdapter staffsAdapter = new StaffsAdapter(staffsList);
                binding.rvStaffs.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rvStaffs.setAdapter(staffsAdapter);
                binding.rvStaffs.setItemAnimator(new DefaultItemAnimator());

                onListener(staffsAdapter, staffsList);
            });
        }

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
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemCencle).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
                    startActivity(new Intent(getContext(), DetailsStaffActivity.class)
                            .putExtra("DATA", staffs)
                            .putExtra("ISEDIT", true));
                    break;
                case R.id.itemDelete:
                    getDataDelete(staffs);
                    break;
                case R.id.itemPrint:
//                    PdfConverters.getInstance(getContext())
//                            .getDataToPdf(binding.getRoot(), staffs.getUid());
                    break;
            }
            return true;
        });
        menu.show();
    }
}