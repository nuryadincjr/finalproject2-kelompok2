package com.nuryadincjr.merdekabelanja.api;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.COLLECTION_PRODUCT;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.TAG;
import static java.util.Objects.requireNonNull;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.models.Products;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ProductsRepository {

    private final CollectionReference collectionReference;

    public ProductsRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(COLLECTION_PRODUCT);
    }

    public Task<Void> deleteProduct(String id) {
        return collectionReference.document(id).delete();
    }

    public Task<Void> insertProducts(Products products) {
        return collectionReference.document(products.getId()).set(products);
    }

    public Task<Void> updateProducts(Products products) {
        return collectionReference.document(products.getId()).set(products);
    }

    public MutableLiveData<Map<String, Object>> getSinggleProduct(Products product) {
        final MutableLiveData<Map<String, Object>> productsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("id", product.getId())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : requireNonNull(task.getResult())) {
                    productsMutableLiveData.postValue(snapshot.getData());
                }
            }else{
                productsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return productsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Products>> getFilterProducts(String[] value) {
        ArrayList<Products> productsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Products>> productsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereIn("category", Arrays.asList(value))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : requireNonNull(task.getResult())) {
                    Products products = document.toObject(Products.class);

                    productsList.add(products);
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

    public MutableLiveData<ArrayList<Products>> getCategoryProducts(
            String value, String fieldName, String[] category) {
        ArrayList<Products> productsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Products>> productsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("category", value)
                .whereIn(fieldName, Arrays.asList(category))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : requireNonNull(task.getResult())) {
                    Products products = document.toObject(Products.class);

                    productsList.add(products);
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

    public MutableLiveData<ArrayList<Products>> getCategoryClothing(
            String category, String people, String fieldName, String[] categoryClothing) {
        ArrayList<Products> productsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Products>> productsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("category", category)
                .whereEqualTo("people", people)
                .whereIn(fieldName, Arrays.asList(categoryClothing))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : requireNonNull(task.getResult())) {
                    Products products = document.toObject(Products.class);

                    productsList.add(products);
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

    public MutableLiveData<ArrayList<Products>> getSearchProducts(String value) {
        ArrayList<Products> productsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Products>> productsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereGreaterThanOrEqualTo("name", value)
                .whereLessThanOrEqualTo("name",value+"~")
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : requireNonNull(task.getResult())) {
                            Products products = document.toObject(Products.class);

                            productsList.add(products);
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
}
