package com.good.pj_0425;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.core.Context;

import java.util.ArrayList;

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

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, UpdateActivity.class);
                intent.putExtra("key",   user.getUser_key());
                intent.putExtra("name",  user.getUser_name());
                intent.putExtra("age",   user.getUser_age());
                context.startActivity(intent);
            }
        });
    }

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