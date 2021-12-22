package com.nuryadincjr.merdekabelanja.api;

import static com.nuryadincjr.merdekabelanja.resorces.Constant.COLLECTION_STAFF;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.TAG;
import static com.nuryadincjr.merdekabelanja.resorces.Constant.time;
import static java.util.Objects.requireNonNull;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.models.Staffs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StaffsRepository {
    private final CollectionReference collectionReference;

    public StaffsRepository() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        collectionReference = db.collection(COLLECTION_STAFF);
    }

    public Task<Void> insertStaffs(Staffs staff) {
          return collectionReference.document(staff.getUid()).set(staff);
    }

    public Task<Void> updateStaffs(Staffs staff) {
        Map<String, Object> data = new HashMap<>();
        data.put("uid", staff.getUid());
        data.put("name", staff.getName());
        data.put("phone", staff.getPhone());
        data.put("email", staff.getEmail());
        data.put("address", staff.getAddress());
        data.put("photo", staff.getPhoto());
        data.put("username", staff.getUsername());
        data.put("latest_update", time());
        return collectionReference.document(staff.getUid()).update(data);
    }

    public Task<Void> deleteStaffs(String id) {
        return collectionReference.document(id).delete();
    }

    public MutableLiveData<ArrayList<Staffs>> getStaffLogin(Staffs staff) {
        ArrayList<Staffs> staffs = new ArrayList<>();
        final MutableLiveData<ArrayList<Staffs>> staffsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("username", staff.getUsername())
                .whereEqualTo("password", staff.getPassword())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : requireNonNull(task.getResult())) {
                    Staffs data = snapshot.toObject(Staffs.class);
                    data.setUid(snapshot.getId());
                    staffs.add(data);
                    Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                }
                staffsMutableLiveData.postValue(staffs);
            }else{
                staffsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return staffsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Staffs>> getFilterStaffs(String[] value) {
        ArrayList<Staffs> staffsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Staffs>> staffsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereIn("division", Arrays.asList(value))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : requireNonNull(task.getResult())) {
                    Staffs staffs = document.toObject(Staffs.class);

                    staffsList.add(staffs);
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                staffsMutableLiveData.postValue(staffsList);
            }
            else{
                staffsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return staffsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Staffs>> getSearchStaffs(String value) {
        ArrayList<Staffs> staffsList = new ArrayList<>();
        final MutableLiveData<ArrayList<Staffs>> staffsMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereGreaterThanOrEqualTo("name", value)
                .whereLessThanOrEqualTo("name",value+"~")
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : requireNonNull(task.getResult())) {
                    Staffs staffs = document.toObject(Staffs.class);

                    staffsList.add(staffs);
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                staffsMutableLiveData.postValue(staffsList);
            }
            else{
                staffsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return staffsMutableLiveData;
    }

    public MutableLiveData<ArrayList<Staffs>> getStaffsData(String uid) {
        ArrayList<Staffs> staffs = new ArrayList<>();
        final MutableLiveData<ArrayList<Staffs>> usersMutableLiveData = new MutableLiveData<>();

        collectionReference
                .whereEqualTo("uid", uid)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : requireNonNull(task.getResult())) {
                    Staffs data = snapshot.toObject(Staffs.class);
                    data.setUid(snapshot.getId());
                    staffs.add(data);
                    Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                }
                usersMutableLiveData.postValue(staffs);
            }else{
                usersMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return usersMutableLiveData;
    }
}
