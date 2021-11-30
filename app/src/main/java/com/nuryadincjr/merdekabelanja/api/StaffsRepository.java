package com.nuryadincjr.merdekabelanja.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.models.Staffs;
import com.nuryadincjr.merdekabelanja.pojo.Constaint;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class StaffsRepository {

    private final FirebaseFirestore db;
    private final String TAG = "LIA";

    public StaffsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Staffs>> getAllStaffs() {
        ArrayList<Staffs> staffs = new ArrayList<>();
        final MutableLiveData<ArrayList<Staffs>> staffsMutableLiveData = new MutableLiveData<>();

        db.collection("staffs").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Staffs staff = document.toObject(Staffs.class);
                    staff.setUid(document.getId());
                    staffs.add(staff);
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                staffsMutableLiveData.postValue(staffs);
            }
            else{
                staffsMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return staffsMutableLiveData;
    }

    public Task<Void> insertStaffs(Staffs staff) {
          return db.collection("staffs").document(staff.getUid()).set(staff);
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
        data.put("latest_update", Constaint.time());
        return db.collection("staffs").document(staff.getUid()).update(data);
    }

    public Task<Void> deleteStaffs(String id) {
        return db.collection("staffs").document(id).delete();
    }

    public MutableLiveData<ArrayList<Staffs>> getStaffLogin(Staffs staff) {
        ArrayList<Staffs> staffs = new ArrayList<>();
        final MutableLiveData<ArrayList<Staffs>> staffsMutableLiveData = new MutableLiveData<>();

        db.collection("staffs")
                .whereEqualTo("username", staff.getUsername())
                .whereEqualTo("password", staff.getPassword())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : task.getResult()) {
                    Staffs data = snapshot.toObject(Staffs.class);
                    data.setUid(snapshot.getId());
                    staffs.add(data);;
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

        db.collection("staffs")
                .whereIn("devision", Arrays.asList(value))
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
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

}
