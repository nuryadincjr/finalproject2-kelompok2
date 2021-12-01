package com.nuryadincjr.merdekabelanja.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.nuryadincjr.merdekabelanja.api.ProductsRepository;
import com.nuryadincjr.merdekabelanja.api.StaffsRepository;
import com.nuryadincjr.merdekabelanja.models.Products;
import com.nuryadincjr.merdekabelanja.models.Staffs;

import java.util.ArrayList;

public class MainViewModel extends AndroidViewModel {

    private final StaffsRepository staffsRepository;
    private final ProductsRepository productsRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.productsRepository = new ProductsRepository();
        this.staffsRepository = new StaffsRepository();
    }

    public MutableLiveData<ArrayList<Products>> getFilterProductsLiveData(String[] value) {
        return productsRepository.getFilterProducts(value);
    }

    public MutableLiveData<ArrayList<Staffs>> getFilterStaffsLiveData(String[] value) {
        return staffsRepository.getFilterStaffs(value);
    }

}
