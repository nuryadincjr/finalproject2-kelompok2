package com.nuryadincjr.merdekabelanja.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nuryadincjr.merdekabelanja.api.AdminsRepository;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.models.Admins;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.models.Users;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final UsersRepository usersRepository;
    private final AdminsRepository adminsRepository;
    private final StaffsRepository staffsRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.usersRepository = new UsersRepository();
        this.adminsRepository = new AdminsRepository();
        this.staffsRepository = new StaffsRepository();
    }

    public MutableLiveData<ArrayList<Users>> getUsersLiveData() {
        return usersRepository.getAllUsers();
    }

    public MutableLiveData<ArrayList<Admins>> getAdminsLiveData() {
        return adminsRepository.getAllAdmins();
    }

    public MutableLiveData<ArrayList<Staffs>> getStaffsLiveData() {
        return staffsRepository.getAllStaffs();
    }

//    public MutableLiveData<ArrayList<Users>> getUserLoginLiveData(Users user) {
//        return usersRepository.getUserLogin(user);
//    }
//
//    public Task<DocumentReference> insertUser(Users user) {
//        return usersRepository.insertUser(user);
//    }

}
