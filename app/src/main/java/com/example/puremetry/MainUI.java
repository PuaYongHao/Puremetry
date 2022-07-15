package com.example.puremetry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainUI extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        Button startButton = (Button) findViewById(R.id.startButton);
        Button resultsButton = (Button) findViewById(R.id.resultsButton);

        startButton.setOnClickListener(this);
        resultsButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.startButton:
                selectProfiles();
                break;
            case R.id.resultsButton:
                selectResults();
                break;
        }
    }

    public void selectProfiles(){
        MainController.loadProfilesList(this);
    }

    public void selectResults(){
        MainController.loadReportsList(this);
    }

}