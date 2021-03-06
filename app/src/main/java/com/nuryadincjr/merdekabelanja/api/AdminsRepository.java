package com.nuryadincjr.merdekabelanja.api;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.COLLECTION_ADMIN;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;
import static java.util.Objects.requireNonNull;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.models.Admins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminsRepository {
    private final CollectionReference collectionReference;

    public AdminsRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(COLLECTION_ADMIN);
    }

    public Task<Void> updateAdmins(Admins admins) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", admins.getUid());
        data.put("name", admins.getName());
        data.put("phone", admins.getPhone());
        data.put("email", admins.getEmail());
        data.put("address", admins.getAddress());
        data.put("photo", admins.getPhoto());
        data.put("username", admins.getUsername());
        data.put("latest_update", time());
        return collectionReference.document(admins.getUid()).update(data);
    }

    public MutableLiveData<ArrayList<Admins>> getAdminLogin(Admins admin) {
        ArrayList<Admins> admins = new ArrayList<>();
        final MutableLiveData<ArrayList<Admins>> adminsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("username", admin.getUsername())
                .whereEqualTo("password", admin.getPassword())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : requireNonNull(task.getResult())) {
                    Admins data = snapshot.toObject(Admins.class);
                    data.setUid(snapshot.getId());
                    admins.add(data);
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

    public MutableLiveData<ArrayList<Admins>> getAdmin(String uid) {
        ArrayList<Admins> admins = new ArrayList<>();
        final MutableLiveData<ArrayList<Admins>> adminsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("uid", uid)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : requireNonNull(task.getResult())) {
                    Admins data = snapshot.toObject(Admins.class);
                    data.setUid(snapshot.getId());
                    admins.add(data);
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
