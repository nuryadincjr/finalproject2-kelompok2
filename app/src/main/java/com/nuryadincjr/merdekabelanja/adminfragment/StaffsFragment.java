package com.nuryadincjr.merdekabelanja.adminfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_FILTER_DIVISION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;

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
import com.nuryadincjr.merdekabelanja.adminacitvity.AddStaffsActivity;
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
    private Set<String> filterStaffs;
    private String[] collect;
    private Menu menu;

    public StaffsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStaffsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Staffs");

        localPreference = LocalPreference.getInstance(getContext());
        collect = getResources().getStringArray(R.array.division);
        filterStaffs = localPreference.getPreferences()
                .getStringSet(KEY_FILTER_DIVISION, new HashSet<>(Arrays.asList(collect)));

        binding.swipeRefresh.setColorSchemeResources(R.color.black);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getData();
            binding.swipeRefresh.setRefreshing(false);
        });

        binding.rvStaffs.addOnScrollListener(getScrollListener());
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddStaffsActivity.class)));

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
                if (dy < 0 && !binding.fabAdd.isShown()) binding.fabAdd.show();
                else if (dy > 0 && binding.fabAdd.isShown()) binding.fabAdd.hide();
                super.onScrolled(recyclerView, dx, dy);
            }
        };
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.menu_category_devisions, menu);
        this.menu = menu;

        SearchView searchView = (SearchView) menu.findItem(R.id.itemSearch).getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setQueryHint("Search");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                getData(s);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                getData(s);
                return false;
            }
        });

        if(filterStaffs.size() == 0 || filterStaffs.size() == 4){
            isSetFilters(true,  true, false);
            filterStaffs.addAll(Arrays.asList(collect));
            getData();
        } else {
            for (String filter : filterStaffs) {
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
                        if(item.isChecked()) filterStaffs.removeAll(Arrays.asList(collect));
                        else filterStaffs.addAll(Arrays.asList(collect));

                        isSetFilters(!item.isChecked(), !item.isChecked(), item.isChecked());
                        break;
                    case R.id.itemFilter1:
                        if(item.isChecked()) filterStaffs.remove(collect[0]);
                        else filterStaffs.add(collect[0]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter2:
                        if(item.isChecked()) filterStaffs.remove(collect[1]);
                        else filterStaffs.add(collect[1]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter3:
                        if(item.isChecked()) filterStaffs.remove(collect[2]);
                        else filterStaffs.add(collect[2]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                    case R.id.itemFilter4:
                        if(item.isChecked()) filterStaffs.remove(collect[3]);
                        else filterStaffs.add(collect[3]);

                        menu.findItem(R.id.itemFilter0).setChecked(false);
                        item.setChecked(!item.isChecked());
                        break;
                }
                localPreference.getEditor().putStringSet(KEY_FILTER_DIVISION, filterStaffs).apply();
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

    private void getData(String name) {
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getSearchStaffs(name).observe(this, staffs -> {
            List<Staffs> staffsList = new ArrayList<>(staffs);
            StaffsAdapter staffsAdapter = new StaffsAdapter(staffsList);

            binding.rvStaffs.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvStaffs.setAdapter(staffsAdapter);
            binding.rvStaffs.setItemAnimator(new DefaultItemAnimator());

            onListener(staffsAdapter, staffsList);
        });
    }

    private void getData() {
        if(filterStaffs.size() !=0) {
            String[] valueList = filterStaffs.toArray(new String[0]);

            MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
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
                StaffsFragment.this.onClick(DetailsStaffActivity.class,
                        staffsList.get(position), null);
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

    @SuppressLint("NonConstantResourceId")
    public void openMenuEditPopup(View view, Staffs staffs) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemCencle).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
                    onClick(AddStaffsActivity.class, staffs, NAME_ISEDIT);
                    break;
                case R.id.itemDelete:
                    getDataDelete(staffs);
                    break;
                case R.id.itemPrint:
                    onClick(DetailsStaffActivity.class, staffs, NAME_ISPRINT);
                    break;
            }
            return true;
        });
        menu.show();
    }

    private <T> void onClick(Class<T> tClass, Object tData, String key) {
        startActivity(new Intent(getContext(), tClass)
                .putExtra(NAME_DATA, (Parcelable) tData)
                .putExtra(key, true));
    }
}