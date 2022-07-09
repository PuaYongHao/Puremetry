package com.example.puremetry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
    }

    public void selectProfiles(View v){
        MainController.loadProfilesList(this);
    }

    public void selectResults(View v){
        MainController.loadReportsList(this);
    }

}