package com.nuryadincjr.merdekabelanja.adminfragment;

import static com.nuryadincjr.merdekabelanja.pojo.ImagesPreference.getStringReplace;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_CATEGORY_DIVISION;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISEDIT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.getStartActivity;
import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.StaffsAdapter;
import com.nuryadincjr.merdekabelanja.adapters.UsersAdapter;
import com.nuryadincjr.merdekabelanja.adminacitvity.AddStaffsActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsStaffActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsUserActivity;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.FragmentStaffsBinding;
import com.nuryadincjr.merdekabelanja.databinding.FragmentUsersBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.resorces.Categoryes;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StaffsFragment extends Fragment {
    private FragmentStaffsBinding binding;
    private LocalPreference localPreference;
    private List<String> divisionType;
    private Categoryes categoryes;

    public StaffsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentStaffsBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Staffs");

        categoryes = Categoryes.getInstance(getContext());
        localPreference = new LocalPreference(requireActivity());

        String divisionTypePreference = localPreference.getPreferences()
                .getString(KEY_CATEGORY_DIVISION, getStringReplace(Arrays.toString(categoryes.division())));
        List<String> collections = asList(divisionTypePreference.split(", ").clone());
        divisionType = new ArrayList<>(collections);
        divisionType.remove("");

        binding.swipeRefresh.setOnRefreshListener(this::onRefresh);
        binding.rvStaffs.addOnScrollListener(getScrollListener());
        binding.fabAdd.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AddStaffsActivity.class)));

        if(savedInstanceState == null) getData();
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

        SubMenu subMenu = menu.getItem(1).getSubMenu();
        subMenu.clear();
        for(int i=0; i <categoryes.division().length; i++){
            subMenu.add(0, i, 0, categoryes.division()[i]);
            subMenu.setGroupCheckable(0, true, false);
        }

        if(divisionType.size() != 0) {
            for (String filter : divisionType) {
                for(int i=0; i<categoryes.division().length; i++){
                    if(filter.equals(categoryes.division()[i])){
                        menu.getItem(1).getSubMenu().getItem(i).setChecked(true);
                    }
                }
            }
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        for (int i=0; i<categoryes.division().length; i++){
            if (item.getItemId() == i){
                if(item.isChecked()) {
                    divisionType.remove(categoryes.division()[item.getItemId()]);
                } else divisionType.add(categoryes.division()[item.getItemId()]);

                localPreference.getEditor()
                        .putString(KEY_CATEGORY_DIVISION, getStringReplace(divisionType))
                        .apply();

                item.setChecked(!item.isChecked());
                getData();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void getData(String name) {
        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getSearchStaffs(name).observe(this, this::onDataSet);
    }

    private void getData() {
        String[] valueList = divisionType.toArray(new String[0]);
        if(divisionType.size() ==0) valueList = categoryes.division();

        MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.getFilterStaffsLiveData(valueList).observe(getViewLifecycleOwner(), this::onDataSet);
    }

    private void getDataDeleted(Staffs staffs) {
        new StaffsRepository().deleteStaffs(staffs.getUid()).addOnSuccessListener(unused -> {
            if (staffs.getPhoto() != null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storage.getReferenceFromUrl(staffs.getPhoto()).delete();
            }
        });
        getData();
    }

    private void onDataSet(ArrayList<Staffs> staffs) {
        List<Staffs> staffsList = new ArrayList<>(staffs);
        StaffsAdapter staffsAdapter = new StaffsAdapter(staffsList);
        binding.rvStaffs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvStaffs.setAdapter(staffsAdapter);
        binding.rvStaffs.setItemAnimator(new DefaultItemAnimator());

        onListener(staffsAdapter, staffsList);
    }

    private void onListener(StaffsAdapter staffsAdapter, List<Staffs> staffsList) {
        staffsAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                getStartActivity(requireContext(),
                        DetailsStaffActivity.class, staffsList.get(position), null);
            }

            @Override
            public void onLongClick(View view, int position) {
                openMenuEditPopup(view, staffsList.get(position));
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    public void openMenuEditPopup(View view, Staffs staffs) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemClose).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
                    getStartActivity(requireContext(),
                            AddStaffsActivity.class, staffs, NAME_ISEDIT);
                    break;
                case R.id.itemDelete:
                    getDataDeleted(staffs);
                    break;
                case R.id.itemPrint:
                    getStartActivity(requireContext(),
                            DetailsStaffActivity.class, staffs, NAME_ISPRINT);
                    break;
            }
            return true;
        });
        menu.show();
    }

    private void onRefresh() {
        getData();
        binding.swipeRefresh.setRefreshing(false);
    }

    public static class UsersFragment extends Fragment {
        private FragmentUsersBinding binding;

        public UsersFragment() {
            // Required empty public constructor
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            binding = FragmentUsersBinding.inflate(inflater, container, false);
            setHasOptionsMenu(true);
            requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Users");

            binding.swipeRefresh.setOnRefreshListener(this::onRefresh);

            if(savedInstanceState == null) getData("");
            return binding.getRoot();
        }

        @Override
        public void onResume() {
            getData("");
            super.onResume();
        }

        @Override
        public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
            inflater.inflate(R.menu.menu_category_devisions, menu);

            menu.findItem(R.id.itemFilter).setVisible(false);
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

            super.onCreateOptionsMenu(menu, inflater);
        }

        private void getData(String name) {
            MainViewModel mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
            mainViewModel.getSearchUsers(name).observe((LifecycleOwner) requireContext(), this::onDataSet);
        }

        private void getDataDeleted(Users users) {
            new UsersRepository().deleteUser(users.getUid()).addOnSuccessListener(unused -> {
                if (users.getPhoto() != null){
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    storage.getReferenceFromUrl(users.getPhoto()).delete();
                }
            });
            getData("");
        }

        private void onDataSet(ArrayList<Users> users) {
            List<Users> usersList = new ArrayList<>(users);
            UsersAdapter usersAdapter = new UsersAdapter(usersList);

            binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvUsers.setAdapter(usersAdapter);
            binding.rvUsers.setItemAnimator(new DefaultItemAnimator());

            onListener(usersAdapter, usersList);
        }

        private void onListener(UsersAdapter usersAdapter, List<Users> usersList) {
            usersAdapter.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position) {
                    UsersFragment.this.onClick(
                            usersList.get(position), null);
                }

                @Override
                public void onLongClick(View view, int position) {
                    openMenuEditPopup(view, usersList.get(position));
                }
            });
        }

        @SuppressLint("NonConstantResourceId")
        public void openMenuEditPopup(View view, Users users) {
            PopupMenu menu = new PopupMenu(view.getContext(), view);
            menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
            menu.getMenu().findItem(R.id.itemEdit).setVisible(false);
            menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
            menu.getMenu().findItem(R.id.itemClose).setVisible(false);

            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.itemDelete:
                        getDataDeleted(users);
                        break;
                    case R.id.itemPrint:
                        onClick(users, NAME_ISPRINT);
                        break;
                }
                return true;
            });
            menu.show();
        }

        private void onClick(Object tData, String key) {
            startActivity(new Intent(getContext(), DetailsUserActivity.class)
                    .putExtra(NAME_DATA, (Parcelable) tData)
                    .putExtra(key, true));
        }

        private void onRefresh() {
            getData("");
            binding.swipeRefresh.setRefreshing(false);
        }
    }
}