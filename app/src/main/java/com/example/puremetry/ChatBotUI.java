package com.example.puremetry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ChatBotUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bot_ui);

        // Remove title
        getSupportActionBar().setTitle("");
    }
}