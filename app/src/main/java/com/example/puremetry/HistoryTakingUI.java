package com.example.puremetry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class HistoryTakingUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_taking_ui);

        // Remove title
        getSupportActionBar().setTitle("");
    }
}