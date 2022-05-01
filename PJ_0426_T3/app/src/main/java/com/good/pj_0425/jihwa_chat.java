package com.good.pj_0425;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class jihwa_chat extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private TextToSpeech tts2;
    private Button btn_Speak2;
    private EditText txtText2;
    private ListView listView2;  //listview
    List fileList2 = new ArrayList<>();
    ArrayAdapter adapter2;
    static boolean calledAlready2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jiwha_chat);

        tts2 = new TextToSpeech(this, this);
        btn_Speak2 = findViewById(R.id.btnSpeak2);
        txtText2 = findViewById(R.id.txtText2);

        btn_Speak2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) { speakOut(); }
        });

//        if (!calledAlready2)
//        {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true); // 다른 인스턴스보다 먼저 실행되어야 한다.
//            calledAlready2 = true;
//        }

        listView2= (ListView)  findViewById(R.id.lv_fileList2);

        adapter2 = new ArrayAdapter<String>(this, R.layout.activity_listitem, fileList2);
        listView2.setAdapter(adapter2);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef  = database.getReference("지화모드");
        DatabaseReference databaseRef = database.getReference("arduino1");
        databaseRef.child("Jihwa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String m = snapshot.getValue(String.class);
                //TextView에 텍스트 가 입력됨
                //txtText2.setText(m);
                // 클래스 모델이 필요?
                for (DataSnapshot fileSnapshot : dataSnapshot.getChildren()) {
                    //MyFiles filename = (MyFiles) fileSnapshot.getValue(MyFiles.class);
                    //하위키들의 value를 어떻게 가져오느냐???
                    String str2 = fileSnapshot.child("jihwamode").getValue(String.class);
                    //plus
                    txtText2.setText(str2);
                    Log.i("TAG: value is ", str2);
                    fileList2.add(str2);
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("TAG: ", "Failed to read value", databaseError.toException());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void speakOut() {
        CharSequence text = txtText2.getText();
        tts2.setPitch((float) 0.7);
        tts2.setSpeechRate((float) 0.8);
        tts2.speak(text,TextToSpeech.QUEUE_FLUSH,null,"id1");
    }

    @Override
    public void onDestroy() {
        if (tts2 != null)  {
            tts2.stop();
            tts2.shutdown();
        }
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)  {
            int result = tts2.setLanguage(Locale.KOREA);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                btn_Speak2.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }
}