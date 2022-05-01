package com.good.pj_0425;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.HashMap;

public class UpdateActivity extends AppCompatActivity {

    EditText updateNameEdit, updateAgeEdit;

    String sKey, sName, sAge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.w("TAG: ", "수정 화면" );
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        DAOUser dao = new DAOUser();

        updateNameEdit = findViewById(R.id.update_name_edit); //이름
        updateAgeEdit = findViewById(R.id.update_age_edit); //번호

        getAndSetIntentData();

        Button updateBtn = findViewById(R.id.update_btn);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("TAG: ", "수정버튼을 눌렀을 때" );
                //변경값
                String uName = updateNameEdit.getText().toString();
                String uAge = updateAgeEdit.getText().toString();

                //파라미터 셋팅
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("title", uName); //마찬가지
                hashMap.put("content", uAge); //생성됐을 때도 content로 바꾸기!!!!!!!!!!!!!!!!!!!!!!!
//                    hashMap.put("user_age", sKey);

                dao.update(sKey, hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "업데이트 성공", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "업데이트 실패:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.w("TAG: ", "수정 화면 탈출 직전" );
            }

        });
        Log.w("TAG: ", "수정 화면 탈출" );
    }

    /**
     * 데이터 가져와서 화면에 보여주기
     */
    public void getAndSetIntentData () {
        Log.w("TAG: ", "데이터 가져와서 수정화면에 보여주기" );
        if (getIntent().hasExtra("key") && getIntent().hasExtra("name") &&
                getIntent().hasExtra("age") ) {

            //데이터 가져오기
            sKey = getIntent().getStringExtra("key");
            sName = getIntent().getStringExtra("name");
            sAge = getIntent().getStringExtra("age");
            Log.w("TAG: ", "데이터 가져오기" );

            //데이터 넣기
            updateNameEdit.setText(sName);
            updateAgeEdit.setText(sAge);
            Log.w("TAG: ", "데이터 넣기" );
        }
    }
}