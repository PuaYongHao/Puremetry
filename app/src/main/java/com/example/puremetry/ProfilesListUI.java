package com.example.puremetry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ProfilesListUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles_list_ui);
        getSupportActionBar().setTitle("");
    }
}