package com.nuryadincjr.merdekabelanja.api;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.models.Users;

import java.util.ArrayList;

public class UsersRepository {

    private FirebaseFirestore db;
    private String TAG = "LIA";

    public UsersRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> insertUser(Users user) {
          return db.collection("users").document(user.getUid()).set(user);
    }

    public Task<Void> updateUser(Users users) {
        return db.collection("users").document(users.getUid()).set(users);
    }

    public MutableLiveData<ArrayList<Users>> getUserLogin(Users user) {
        ArrayList<Users> users = new ArrayList<>();
        final MutableLiveData<ArrayList<Users>> usersMutableLiveData = new MutableLiveData<>();

        db.collection("users")
                .whereEqualTo("phone", user.getPhone())
                .whereEqualTo("password", user.getPassword())
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : task.getResult()) {
                    Users data = snapshot.toObject(Users.class);
                    data.setUid(snapshot.getId());
                    users.add(data);;
                    Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                }
                usersMutableLiveData.postValue(users);
            }else{
                usersMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return usersMutableLiveData;
    }

    public MutableLiveData<ArrayList<Users>> getUserData(String uid) {
        ArrayList<Users> users = new ArrayList<>();
        final MutableLiveData<ArrayList<Users>> usersMutableLiveData = new MutableLiveData<>();

        db.collection("users")
                .whereEqualTo("uid", uid)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot  snapshot : task.getResult()) {
                    Users data = snapshot.toObject(Users.class);
                    data.setUid(snapshot.getId());
                    users.add(data);;
                    Log.d(TAG, snapshot.getId() + " => " + snapshot.getData());
                }
                usersMutableLiveData.postValue(users);
            }else{
                usersMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });
        return usersMutableLiveData;
    }
}
