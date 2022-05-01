package com.good.pj_0425;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Context;

import java.util.ArrayList;

//어댑터 : 데이터와 화면을 연결해주는 기능
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserVH>{

    private ListActivity context;

    ArrayList<User> list = new ArrayList<>();

    public UserAdapter(ListActivity context, ArrayList<User> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public UserVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

        return new UserVH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserVH holder, int position) {

        User user = list.get(holder.getAdapterPosition());

        //이름
        holder.nameText.setText(user.getUser_name());
        Log.w("TAG: ", "리스트 목록 화면으로 들어왔을 때" );
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("TAG: ", "목록 중 하나를 눌렸을 때" );
                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("key",   user.getUser_key());
                intent.putExtra("name",  user.getUser_name());
                intent.putExtra("content",   user.getUser_age());
                context.startActivity(intent);
            }
        });
        Log.w("TAG: ", "리스트 onclick 탈출" );
    }//onBindViewHolder

    @Override
    public int getItemCount() {
        return list.size();
    }


    class UserVH extends RecyclerView.ViewHolder {

        TextView nameText;

        CardView cardView;

        public UserVH(@NonNull View itemView) {
            super(itemView);

            nameText = itemView.findViewById(R.id.name_text);
            cardView = itemView.findViewById(R.id.card_view);
        }

//        public int getBindingAdapterPosition() {
//
//        }
    }
}