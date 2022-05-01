package com.good.pj_0425;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class ListActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    UserAdapter adapter;

    //데이터 베이스 객체
    DAOUser dao;

    //키 변수
    String key = "";

    ArrayList<User> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        recyclerView = findViewById(R.id.rv);

        recyclerView.setHasFixedSize(true);

        //화면 설정
        LinearLayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);

        //어뎁터 설정
        adapter = new UserAdapter(this, list);

        //리싸이클러뷰 어뎁터 설정
        recyclerView.setAdapter(adapter);

        //데이터베이스 초기화
        dao = new DAOUser();

        //데이터 가져오기
        loadData();

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Log.w("TAG: ", "onMove 안" );
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Log.w("TAG: ", "onSwiped 안" );
                int position = viewHolder.getBindingAdapterPosition();

                switch(direction){

                    case ItemTouchHelper.LEFT:

                        String key = list.get(position).getUser_key();

                        DAOUser dao = new DAOUser();

                        dao.remove(key).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(ListActivity.this, "삭제 성공", Toast.LENGTH_SHORT).show();
                            }

                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ListActivity.this, "삭제 실패:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder,
                        dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(Color.RED)
                        .addSwipeLeftActionIcon(R.drawable.ic_delete)
                        .addSwipeLeftLabel("삭제")
                        .setSwipeLeftLabelColor(Color.WHITE)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerView);
    }

    private void loadData() {
        Log.w("TAG: ", "목록 데이터 가져오기 시작" );
        dao.get().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list.clear();

                for(DataSnapshot data : snapshot.getChildren()){

                    User user = data.getValue(User.class);

                    //키 값 가져오기
                    key = data.getKey();

                    //키 값 담기
                    user.setUser_key(key);

                    //리스트에 담기
                    list.add(user);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        Log.w("TAG: ", "목록 데이터 가져오기 끝" );
    }
}