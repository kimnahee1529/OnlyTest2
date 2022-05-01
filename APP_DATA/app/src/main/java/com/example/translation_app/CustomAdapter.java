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
    private int listNumber=0;

    //파이어베이스 연동
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference titleRef = database.getReference("title");
    DatabaseReference contentRef = database.getReference("content");

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
        //데이터들이 로드될 때 setText로 보여주는 것
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

        //원래 이거
//        listNumber++;
//
//        titleRef.child(Integer.toString(listNumber)).setValue(holder.tv_title.getText().toString());
//        contentRef.child(Integer.toString(listNumber)).setValue(holder.tv_content.getText().toString());

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
            //itemView는 목록 하나
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            // 주석 해제
            listNumber++;
            titleRef.child(Integer.toString(listNumber)).setValue(tv_title.getText().toString());
            contentRef.child(Integer.toString(listNumber)).setValue(tv_content.getText().toString());

            Log.w("TAG: ", "추가했을 때 : +버튼 누르고 확인 버튼 눌렀을 때");

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //추가된 항목 눌렀을 때
                    Log.w("TAG: ", "추가된 항목 눌렀을 때");
                    int curPos = getAdapterPosition();  //현재 리스트 클릭한 아이템 위치
//                    Log.w("TAG: ", "msg", curPos);
                    TodoItem todoItem = mTodoItems.get(curPos);

                    String[] strChoiceItems = {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택 해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position) {
                            //리스트 부분에 데이터가 있을 때, 리스트 화면 나갔다 들어와도 이전 기록 띄우기 위한 부분
                            Log.w("TAG: ", "수정하기 버튼 눌렀을 때");
                            for(int i=0; i<listNumber; i++){
                                if(listNumber==0){
                                    break;
                                }else{

                                }
                            }
//                            if () {
//=='\0'
//                            Log.w("TAG: ", "확인용 msg", );
//                                Log.d(database.getReference().child("title").getKey());
//                            }else {
                                if (position == 0) {
                                    //수정
                                    //팝업 창 띄우기
                                    Log.w("TAG: ", "수정하기 눌렀을 때");
                                    Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                    dialog.setContentView(R.layout.dialog_edit);
                                    EditText et_title = dialog.findViewById(R.id.et_title);
                                    EditText et_content = dialog.findViewById(R.id.et_content);
                                    Button btn_ok = dialog.findViewById(R.id.btn_ok);
                                    btn_ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            //update table
                                            Log.w("TAG: ", "수정할 데이터 쓰고 확인 누르기");
                                            String title = et_title.getText().toString();
                                            String content = et_content.getText().toString();
                                            String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());    //현재 date 받아오기
                                            String beforeTime = todoItem.getWriteDate();

                                            //2. 파이어베이스의 데이터 수정하기
//                                            titleRef.setValue(tv_title.getText().toString());
//                                            contentRef.setValue(tv_content.getText().toString());
//                                            int v1 = Log.v(todoItem.getWriteDate());
//                                            Log.w("TAG: ", beforeTime);
                                            //titleRef.setValue(tv_title.getWriteDate().toString());
                                            titleRef.child(title).setValue(tv_title.getText().toString());
                                            contentRef.child(content).setValue(tv_content.getText().toString());

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
                                    Log.w("TAG: ", "목록을 누르고 삭제하기 버튼을 눌렀을 때");
                                    String beforeTime = todoItem.getWriteDate();
                                    //delete UI
                                    mTodoItems.remove(curPos);
                                    notifyItemRemoved(curPos);
                                    Toast.makeText(mContext, "목록이 제거 되었습니다", Toast.LENGTH_SHORT).show();

                                    //3. 파이어베이스의 데이터 삭제하기
//                                titleRef.setValue(tv_title.getText().toString());
//                                contentRef.setValue(tv_content.getText().toString());
//                                    database.getReference().child("title").removeValue();
//                                    database.getReference().child("content").removeValue();
                                    titleRef.child(Integer.toString(listNumber)).removeValue();
                                    contentRef.child(Integer.toString(listNumber)).removeValue();
                                }
                                else{
                                    Log.w("TAG: ", "여긴 그냥 else");
                                    //수정,삭제가 아닐 때 그냥 업로드
                                    listNumber++;
                                    titleRef.child(Integer.toString(listNumber)).setValue(tv_title.getText().toString());
                                    contentRef.child(Integer.toString(listNumber)).setValue(tv_content.getText().toString());
                                }
                            }
//                        }
                    });
                    builder.show();

                }
            });
        }

    }

    // 액티비티에서 호출되는 함수이며, 현재 어탭터에 새로운 게시글 아이템을 전달받아 추가하는 ㅅ목적
    public  void addItem(TodoItem _item){
        mTodoItems.add(0, _item);
        notifyItemInserted(0);
    }

}
