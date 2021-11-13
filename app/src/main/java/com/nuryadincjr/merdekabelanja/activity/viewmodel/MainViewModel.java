package com.nuryadincjr.merdekabelanja.activity.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nuryadincjr.merdekabelanja.activity.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.pojo.Users;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final UsersRepository usersRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.usersRepository = new UsersRepository();
    }

    public MutableLiveData<ArrayList<Users>> getUsersMutableLiveData() {
        return usersRepository.getAllUsers();
    }

//    public Task<DocumentReference> insertUser(Users user) {
//        return usersRepository.insertUser(user);
//    }

}
