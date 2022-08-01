package com.example.puremetry;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainUI extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_ui);

        // Hide title bar
        getSupportActionBar().hide();

        // Set OnClickListener for button
        Button startButton = findViewById(R.id.startButton);
        Button resultsButton = findViewById(R.id.resultsButton);

        startButton.setOnClickListener(this);
        resultsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startButton:
                selectProfiles();
                break;
            case R.id.resultsButton:
                selectResults();
                break;
        }
    }

    public void selectProfiles() {
        MainController.loadProfilesList(this);
    }

    public void selectResults() {
        MainController.loadReportsList(this);
    }

}