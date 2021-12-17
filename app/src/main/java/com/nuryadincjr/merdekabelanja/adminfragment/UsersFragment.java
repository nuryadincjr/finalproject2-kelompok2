package com.nuryadincjr.merdekabelanja.adminfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISPRINT;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.google.firebase.storage.FirebaseStorage;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.adapters.UsersAdapter;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsStaffActivity;
import com.nuryadincjr.merdekabelanja.adminacitvity.DetailsUserActivity;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.FragmentUsersBinding;
import com.nuryadincjr.merdekabelanja.interfaces.ItemClickListener;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.viewmodel.MainViewModel;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    private FragmentUsersBinding binding;

    public UsersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUsersBinding.inflate(inflater, container, false);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Users");

        binding.swipeRefresh.setColorSchemeResources(R.color.black);
        binding.swipeRefresh.setOnRefreshListener(() -> {
            getData("");
            binding.swipeRefresh.setRefreshing(false);
        });

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
        mainViewModel.getSearchUsers(name).observe((LifecycleOwner) getContext(), users -> {
            List<Users> usersList = new ArrayList<>(users);
            UsersAdapter usersAdapter = new UsersAdapter(usersList);

            binding.rvUsers.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.rvUsers.setAdapter(usersAdapter);
            binding.rvUsers.setItemAnimator(new DefaultItemAnimator());

            onListener(usersAdapter, usersList);
        });
    }

    private void onListener(UsersAdapter usersAdapter, List<Users> usersList) {
        usersAdapter.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                UsersFragment.this.onClick(DetailsStaffActivity.class,
                        usersList.get(position), null);
            }

            @Override
            public void onLongClick(View view, int position) {
                openMenuEditPopup(view, usersList.get(position));
            }
        });
    }

    private void getDataDelete(Users users) {
        new UsersRepository().deleteUser(users.getUid()).addOnSuccessListener(unused -> {
            if (users.getPhoto() != null){
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storage.getReferenceFromUrl(users.getPhoto()).delete();
            }
        });
        getData("");
    }

    @SuppressLint("NonConstantResourceId")
    public void openMenuEditPopup(View view, Users users) {
        PopupMenu menu = new PopupMenu(view.getContext(), view);
        menu.getMenuInflater().inflate(R.menu.menu_edit, menu.getMenu());
        menu.getMenu().findItem(R.id.itemSaves).setVisible(false);
        menu.getMenu().findItem(R.id.itemCencle).setVisible(false);

        menu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.itemEdit:
//                    onClick(RegisterActivity.class, users, NAME_ISEDIT);
                    break;
                case R.id.itemDelete:
                    getDataDelete(users);
                    break;
                case R.id.itemPrint:
                    onClick(DetailsUserActivity.class, users, NAME_ISPRINT);
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