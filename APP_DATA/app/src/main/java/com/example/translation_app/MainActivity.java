package com.example.translation_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    private Button jihwa_button;
    private Button suhwa_button;
    private Button list_btn;

    Switch bluetooth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jihwa_button = (Button)findViewById(R.id.jihwa_button);
        suhwa_button = (Button)findViewById(R.id.suhwa_button);
        list_btn = (Button)findViewById(R.id.list_btn);

        jihwa_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, jihwa_chat.class);
                startActivity(intent);
            }
        });

        suhwa_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, suhwa_chat.class);
                startActivity(intent);
            }
        });

        list_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, suhwa_list.class);
                startActivity(intent);
            }
        });

    }
}