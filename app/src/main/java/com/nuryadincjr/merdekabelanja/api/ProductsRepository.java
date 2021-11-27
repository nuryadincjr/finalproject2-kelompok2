package com.nuryadincjr.merdekabelanja.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.models.Products;

import java.util.ArrayList;
import java.util.Map;

public class ProductsRepository {

    private final FirebaseFirestore db;
    private final String TAG = "LIA";

    public ProductsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Products>> getAllProducts() {
        ArrayList<Products> productsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Products>> productsMutableLiveData = new MutableLiveData<>();

        db.collection("products").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Products products = document.toObject(Products.class);

                    productsList.add(products);;
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                productsMutableLiveData.postValue(productsList);
            }
            else{
                productsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return productsMutableLiveData;
    }

    public Task<Void> deleteProduct(String id) {
        return db.collection("products").document(id).delete();
    }

    public Task<Void> insertProducts(Products products) {
        return db.collection("products").document(products.getId()).set(products);
    }

    public Task<Void> updateProducts(Products products) {
        return db.collection("products").document(products.getId()).set(products);
    }

    public MutableLiveData<Map<String, Object>> getSinggleProduct(Products product) {

        final MutableLiveData<Map<String, Object>> staffsMutableLiveData = new MutableLiveData<>();
        db.collection("products")
                .whereEqualTo("id", product.getId())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : task.getResult()) {
                staffsMutableLiveData.postValue(snapshot.getData());
                }
            }else{
                staffsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return staffsMutableLiveData;
    }
}
