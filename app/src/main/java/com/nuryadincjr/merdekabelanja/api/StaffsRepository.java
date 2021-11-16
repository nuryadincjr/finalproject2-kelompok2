package com.nuryadincjr.merdekabelanja.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.pojo.Staffs;

import java.util.ArrayList;

public class StaffsRepository {

    private FirebaseFirestore db;
    private String TAG = "LIA";

    public StaffsRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Staffs>> getAllStaffs() {
        ArrayList<Staffs> staffs = new ArrayList<>();;
        final MutableLiveData<ArrayList<Staffs>> staffsMutableLiveData = new MutableLiveData<>();

        db.collection("staffs").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Staffs staff = document.toObject(Staffs.class);
                    staff.setUid(document.getId());
                    staffs.add(staff);;
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
}
