package com.example.puremetry;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class NoiseDetectUI extends AppCompatActivity implements View.OnClickListener {

    private int noiseLevel;
    private String[] noise_description = {
            "This is more likely to damage your hearing over time.",
            "Find a quiet place.",
            "It is good to go."
    };
    private ProgressBar noiseLevelProgressBar;
    private TextView noiseDetectTextView;
    private TextView noiseLevelTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noise_detect_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        noiseLevelProgressBar = findViewById(R.id.noiseLevelProgressBar);
        noiseDetectTextView = findViewById(R.id.noiseDetectTextView);
        noiseLevelTextView = findViewById(R.id.noiseLevelTextView);

        // Set OnClickListener for button
        Button continueButton = findViewById(R.id.continueButton);

        continueButton.setOnClickListener(this);

        noiseLevel = 0;
        noiseLevelProgressBar.setProgress(noiseLevel);

        displayNoiseLevel();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                noiseLevel += 5;
                displayNoiseLevel();
                break;
        }
    }

    public void displayNoiseLevel() {
        noiseLevelProgressBar.setProgress(noiseLevel);
        noiseLevelTextView.setText(noiseLevel + " dB");
        if (noiseLevel >= 70)
            noiseDetectTextView.setText(noise_description[0]);
        else if (noiseLevel < 20)
            noiseDetectTextView.setText(noise_description[2]);
        else
            noiseDetectTextView.setText(noise_description[1]);
    }
}