package com.good.pj_0425;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class AddActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        EditText name_edit = findViewById(R.id.name_edit);
        EditText age_edit = findViewById(R.id.age_edit);
        Button addBtn = findViewById(R.id.add_btn);

        DAOUser dao = new DAOUser();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //입력값 변수에 담기
                String name = name_edit.getText().toString(); //이름
                String age = age_edit.getText().toString(); // 나이

                User user = new User("", name, age);

                //데이터베이스 사용자 등록
                dao.add(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "성공", Toast.LENGTH_SHORT).show();

                        //입력창 초기화
                        name_edit.setText("");
                        age_edit.setText("");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "실패:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }//onClick
        });

        //리스트 버튼
        Button listBtn = findViewById(R.id.list_btn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, ListActivity.class);
                startActivity(intent);
            }
        });
    }//onCreate
}