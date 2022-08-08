package com.example.puremetry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InstructionsUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions_ui);

        // Remove title
        getSupportActionBar().setTitle("");

    }
}