package com.nuryadincjr.merdekabelanja.usersfragment;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.KEY_UID;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_DATA;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_ISLOGIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.NAME_UID;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import java.util.Objects;

public class UserProfileFragment extends Fragment {
    private FragmentUserProfileBinding binding;
    private LocalPreference localPreference;
    private Users users = new Users();
    private final Handler headlineHandler = new Handler();

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUserProfileBinding.inflate(inflater, container, false);

        localPreference = LocalPreference.getInstance(getContext());
        String uid = localPreference.getPreferences().getString(KEY_UID, "");

        binding.llLogout.setOnClickListener(this::onClick);
        binding.llMyInfo.setOnClickListener(v -> onClick(MyInfoActivity.class));
        binding.llSign.setOnClickListener(v -> onClick(SecurityActivity.class));
        binding.llAbout.setOnClickListener(v -> startActivity(new Intent(getContext(), AboutActivity.class)));
        binding.llMyChart.setOnClickListener(v -> {});
        binding.llPurchaseHistory.setOnClickListener(v -> {});
        binding.llCostumerServices.setOnClickListener(v -> {});

        getData(uid);
        return binding.getRoot();
    }

    private void getData(String uid) {
        Runnable headlineRunnable = () -> new UsersRepository()
                        .getUserData(uid)
                        .observe(requireActivity(), this::onDataSet);
        headlineHandler.post(headlineRunnable);
    }

    private <T> void onClick(Class<T> tClass) {
        startActivity(new Intent(getContext(), tClass)
                .putExtra(NAME_DATA, users));
    }

    private void onClick(View v) {
        localPreference.getEditor()
                .putInt(NAME_ISLOGIN, 0)
                .putString(NAME_UID, null).apply();
        startActivity(new Intent(getContext(), LoggedOutActivity.class));
        Objects.requireNonNull(requireActivity()).finishAffinity();
    }

    private void onDataSet(ArrayList<Users> user) {
        if (user.size() != 0) {
            users = user.get(0);
            Glide.with(requireContext())
                    .load(users.getPhoto())
                    .centerCrop()
                    .placeholder(R.drawable.ic_brand)
                    .into(binding.ivPhoto);
            binding.tvName.setText(users.getName());
            binding.tvPhone.setText(users.getPhone());
        }
    }
}