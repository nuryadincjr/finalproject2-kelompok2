package com.nuryadincjr.merdekabelanja.usersfragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.nuryadincjr.merdekabelanja.R;
import com.nuryadincjr.merdekabelanja.activity.AboutActivity;
import com.nuryadincjr.merdekabelanja.activity.LoggedOutActivity;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.databinding.FragmentUserProfileBinding;
import com.nuryadincjr.merdekabelanja.models.Users;
import com.nuryadincjr.merdekabelanja.pojo.LocalPreference;
import com.nuryadincjr.merdekabelanja.usrsactivity.MyInfoActivity;
import com.nuryadincjr.merdekabelanja.usrsactivity.SecurityActivity;

import java.util.ArrayList;

public class UserProfileFragment extends Fragment {

    private FragmentUserProfileBinding binding;
    private LocalPreference localPreference;
    private Users users = new Users();

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);

        localPreference = LocalPreference.getInstance(getContext());
        String uid = localPreference.getPreferences().getString("UID", "");

        uid = "71DKdinEJVTbs2sORDwnsRzGBqJ3";
        onDataSet(uid);

        binding.llLogiut.setOnClickListener(v -> {
            localPreference.getEditor()
                    .putInt("ISLOGIN", 0)
                    .putString("UID", null).apply();
            startActivity(new Intent(getContext(), LoggedOutActivity.class));
            getActivity().finishAffinity();
        });

        binding.llMyInfo.setOnClickListener(v ->
                startActivity(new Intent(getContext(), MyInfoActivity.class).
                        putExtra("DATA", users)));
        binding.llSign.setOnClickListener(v ->
                startActivity(new Intent(getContext(), SecurityActivity.class).
                        putExtra("DATA", users)));
        binding.llAbout.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AboutActivity.class)));
        return binding.getRoot();
    }

    private void onDataSet(String uid) {
        new UsersRepository().getUserData(uid).observe(getActivity(), (ArrayList<Users> user) -> {
            if(user.size() != 0) {
                users = user.get(0);
                Glide.with(this)
                        .load(users.getPhoto())
                        .centerCrop()
                        .placeholder(R.drawable.ic_brand)
                        .into(binding.ivPhoto);
                binding.tvName.setText(users.getName());
                binding.tvPhone.setText(users.getPhone());
            }
        });
    }
}