package com.example.translation_app;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Ref;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<TodoItem> mTodoItems;
    private Context mContext;
    private int listNumber=1;

    //파이어베이스 연동
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference titleRef = database.getReference("list");
    DatabaseReference contentRef = database.getReference("list");

    public CustomAdapter(ArrayList<TodoItem> mTodoItems, Context mContext){
        this.mTodoItems = mTodoItems;
        this.mContext = mContext;
    }
    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position) {

        holder.tv_title.setText(mTodoItems.get(position).getTitle());
        holder.tv_content.setText(mTodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mTodoItems.get(position).getWriteDate());

        //1. 파이어베이스로 데이터 올리기
//        titleRef.setValue(holder.tv_title.getText().toString());
//        contentRef.setValue(holder.tv_content.getText().toString());
//        titleRef.child("title").push().setValue(holder.tv_title.getText().toString());
//        contentRef.child("content").push().setValue(holder.tv_content.getText().toString());
        //이건 됨
//        titleRef.push().setValue(holder.tv_title.getText().toString());
//        contentRef.push().setValue(holder.tv_content.getText().toString());
        //

//        titleRef.child(Integer.toString(listNumber)).setValue(holder.tv_title.getText().toString());
//        contentRef.child(Integer.toString(listNumber)).setValue(holder.tv_content.getText().toString());
//        listNumber++;
//        titleRef.child("content").child("object1").setValue("1");
        titleRef.child("object"+Integer.toString(listNumber)).child("title").setValue(holder.tv_title.getText().toString());
        contentRef.child("object"+Integer.toString(listNumber)).child("content").setValue(holder.tv_content.getText().toString());
        listNumber++;
//        Integer.toString(listNumber)
//        .child(Integer.toString(listNumber))
    }

    @Override
    public int getItemCount() {
        return mTodoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_title;
        private TextView tv_content;
        private TextView tv_writeDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int curPos = getAdapterPosition();  //현재 리스트 클릭한 아이템 위치
                    TodoItem todoItem = mTodoItems.get(curPos);
                    Log.w("TAG: ", "CURPOS"+curPos);
                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택 해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            //리스트 부분에 데이터가 있을 때, 리스트 화면 나갔다 들어와도 이전 기록 띄우기 위한 부분
//                            if () {
//=='\0'
//                            Log.w("TAG: ", "확인용 msg", );
//                                Log.d(database.getReference().child("title").getKey());
//                            }else {
                                if (position == 0) {
                                    //수정
                                    //팝업 창 띄우기
                                    Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                    dialog.setContentView(R.layout.dialog_edit);
                                    EditText et_title = dialog.findViewById(R.id.et_title);
                                    EditText et_content = dialog.findViewById(R.id.et_content);
                                    Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                    btn_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //update table
                                            String title = et_title.getText().toString();
                                            String content = et_content.getText().toString();
                                            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());    //현재 date 받아오기
                                            String beforeTime = todoItem.getWriteDate();

                                            //2. 파이어베이스의 데이터 수정하기
                                            //밑의 줄 setValue가 추가하는 건데 수정에 넣어놔서 그런 듯???
                                            titleRef.setValue(tv_title.getText().toString());
                                            contentRef.setValue(tv_content.getText().toString());
//                                            이 밑 줄 수정하다가 말음
//                                            titleRef.child("list").child("object"+listNumber).child("content").updateChildren();
//                                            contentRef.child("list").child("object"+listNumber).child("title");



                                            //update UI
                                            todoItem.setTitle(title);
                                            todoItem.setContent(content);
                                            todoItem.setWriteDate(currentTime);
                                            notifyItemChanged(curPos, todoItem);
                                            dialog.dismiss();
                                            Toast.makeText(mContext, "목록 수정이 완료 되었습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                    dialog.show();
                                } else if (position == 1) {
                                    //삭제 delete table
                                    String beforeTime = todoItem.getWriteDate();
                                    //delete UI

                                    mTodoItems.remove(curPos);
                                    notifyItemRemoved(curPos);
                                    Toast.makeText(mContext, "목록이 제거 되었습니다", Toast.LENGTH_SHORT).show();

                                    //3. 파이어베이스의 데이터 삭제하기
//                                titleRef.setValue(tv_title.getText().toString());
//                                contentRef.setValue(tv_content.getText().toString());
//                                    dataSnapshot.getRef().removeValue();
//                                    밑 줄도 다 삭제
//                                titleRef.removeValue();
//                                contentRef.removeValue();


//                                     밑 줄은 전체 삭제
//                                    database.getReference().child("title").removeValue();
//                                    database.getReference().child("content").removeValue();
                                    
//                                    titleRef.child(Integer.toString(listNumber)).removeValue();
//                                    database.getReference().child("content").child(Integer.toString(listNumber)).removeValue();
//                                    contentRef.child(Integer.toString(listNumber)).removeValue();
                                }
                            }
//                        }
                    });
                    builder.show();

                }
            });
        }

    }

    // 액티비티에서 호출되는 함수이며, 현재 어탭터에 새로운 게시글 아이템을 전달받아 추가하는 목적
    public  void addItem(TodoItem _item){
        mTodoItems.add(0, _item);
        notifyItemInserted(0);
    }

}
