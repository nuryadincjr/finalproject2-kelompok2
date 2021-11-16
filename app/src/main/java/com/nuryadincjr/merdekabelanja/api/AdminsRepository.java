package com.nuryadincjr.merdekabelanja.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.pojo.Admins;

import java.util.ArrayList;

public class AdminsRepository {

    private FirebaseFirestore db;
    private String TAG = "LIA";

    public AdminsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Admins>> getAllAdmins() {
        ArrayList<Admins> admins = new ArrayList<>();;
        final MutableLiveData<ArrayList<Admins>> adminsMutableLiveData = new MutableLiveData<>();

        db.collection("admins").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Admins admin = document.toObject(Admins.class);
                    admin.setUid(document.getId());
                    admins.add(admin);;
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                adminsMutableLiveData.postValue(admins);
            }
            else{
                adminsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return adminsMutableLiveData;
    }

    public Task<Void> insertAdmin(Admins admin) {
          return db.collection("admins").document(admin.getUid()).set(admin);
    }

    public MutableLiveData<ArrayList<Admins>> getAdminLogin(Admins admin) {
        ArrayList<Admins> admins = new ArrayList<>();
        final MutableLiveData<ArrayList<Admins>> adminsMutableLiveData = new MutableLiveData<>();

        db.collection("admins")
                .whereEqualTo("username", admin.getUsername())
                .whereEqualTo("password", admin.getPassword())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : task.getResult()) {
                    Admins data = snapshot.toObject(Admins.class);
                    data.setUid(snapshot.getId());
                    admins.add(data);;
                    Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                }
                adminsMutableLiveData.postValue(admins);
            }else{
                adminsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return adminsMutableLiveData;
    }
}
