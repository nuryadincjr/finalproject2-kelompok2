package com.nuryadincjr.merdekabelanja.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.pojo.Users;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final UsersRepository usersRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.usersRepository = new UsersRepository();
    }

    public MutableLiveData<ArrayList<Users>> getUsersLiveData() {
        return usersRepository.getAllUsers();
    }

    public MutableLiveData<ArrayList<Users>> getUserLoginLiveData(Users user) {
        return usersRepository.getUserLogin(user);
    }

//    public Task<DocumentReference> insertUser(Users user) {
//        return usersRepository.insertUser(user);
//    }

}
