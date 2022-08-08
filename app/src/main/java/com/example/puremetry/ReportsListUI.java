package com.example.puremetry;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class ReportsListUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_list_ui);

        // Remove title
        getSupportActionBar().setTitle("");
    }
}