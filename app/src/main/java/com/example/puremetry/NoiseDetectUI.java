package com.example.puremetry;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class NoiseDetectUI extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_detect_ui);

        // Remove title
        getSupportActionBar().setTitle("");
    }
}