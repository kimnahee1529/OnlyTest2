package com.good.pj_0425;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;

public class DAOUser {

    private DatabaseReference databaseReference;

    DAOUser(){

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        databaseReference = db.getReference(User.class.getSimpleName());
    }


    //등록
    public Task<Void> add(User user){
        return databaseReference.push().setValue(user);
    }

    //조회
    public Query get(){
        return  databaseReference;
    }

    //수정
    public Task<Void> update(String key, HashMap<String, Object> hashMap){
        return databaseReference.child(key).updateChildren(hashMap);
    }

    //삭제
    public Task<Void> remove(String key){
        return databaseReference.child(key).removeValue();
    }

}
