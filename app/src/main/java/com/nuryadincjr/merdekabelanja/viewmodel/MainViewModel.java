package com.nuryadincjr.merdekabelanja.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.api.UsersRepository;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.models.Users;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final StaffsRepository staffsRepository;
    private final ProductsRepository productsRepository;
    private final UsersRepository usersRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.usersRepository = new UsersRepository();
        this.productsRepository = new ProductsRepository();
        this.staffsRepository = new StaffsRepository();
    }

    public MutableLiveData<ArrayList<Products>> getFilterProductsLiveData(String[] value) {
        return productsRepository.getFilterProducts(value);
    }

    public MutableLiveData<ArrayList<Products>> getCategoryProductsLiveData(
            String value, String fildname, String[] category) {
        return productsRepository.getCategoryProducts(value, fildname, category);
    }

    public MutableLiveData<ArrayList<Staffs>> getSearchStaffs(String value) {
        return staffsRepository.getSearchStaffs(value);
    }

    public MutableLiveData<ArrayList<Users>> getSearchUsers(String value) {
        return usersRepository.getSearchUsers(value);
    }


    public MutableLiveData<ArrayList<Products>> getSearchProducts(String value) {
        return productsRepository.getSearchProducts(value);
    }

    public MutableLiveData<ArrayList<Staffs>> getFilterStaffsLiveData(String[] value) {
        return staffsRepository.getFilterStaffs(value);
    }
}
