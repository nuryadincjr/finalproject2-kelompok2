package com.nuryadincjr.merdekabelanja.activity.api;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.nuryadincjr.merdekabelanja.pojo.Users;

import java.util.ArrayList;

public class UsersRepository {

    private FirebaseFirestore db;


    public UsersRepository() {
        db = FirebaseFirestore.getInstance();
    }

    public MutableLiveData<ArrayList<Users>> getAllUsers() {
        ArrayList<Users> users = new ArrayList<>();;
        final MutableLiveData<ArrayList<Users>> usersMutableLiveData = new MutableLiveData<>();

        db.collection("users").get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Users user = document.toObject(Users.class);
                    user.setUid(document.getId());
                    users.add(user);;
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
                usersMutableLiveData.postValue(users);
            }
            else
                usersMutableLiveData.setValue(null);
                Log.w(TAG, "Error getting documents.", task.getException());

        });
        return usersMutableLiveData;
    }

    public Task<DocumentReference> insertUser(Users user) {
          return db.collection("users").add(user);
    }

}
