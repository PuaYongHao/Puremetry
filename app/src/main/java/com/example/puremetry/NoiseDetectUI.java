package com.example.puremetry;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class NoiseDetectUI extends AppCompatActivity implements View.OnClickListener {

    private int noiseLevel = 0;
    private ProgressBar noiseLevelProgressBar;
    private TextView noiseDetectTextView;
    private TextView noiseLevelTextView;
    private Button continueButton;
    private Handler handler = new Handler();
    private AudioRecord recorder;

    public static double REFERENCE = 0.00002;
    public static final int APP_PERMISSION_RECORD_AUDIO = 1;

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
        continueButton = findViewById(R.id.continueButton);
        continueButton.setOnClickListener(this);
        continueButton.setEnabled(false);

        noiseLevelProgressBar.setProgress(noiseLevel);

        detectNoiseLevel();
    }

    final Runnable updater = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 500);
            if (recorder != null)
                detectNoiseLevel();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continueButton:
                NoiseDetectController.loadHearingTest(this);
                break;
        }
    }

    public void displayNoiseLevel() {
        noiseLevelProgressBar.setProgress(noiseLevel);
        noiseLevelTextView.setText(noiseLevel + " dB");
        String noise_description = NoiseDetectController.updateDescription(noiseLevel);
        noiseDetectTextView.setText(noise_description);
    }

    public void detectNoiseLevel() {
        if (NoiseDetectController.checkPermission(this)) {
            int bufferSize = AudioRecord.getMinBufferSize(44100, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            bufferSize = bufferSize * 4;
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    44100, AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT, bufferSize);

            short data[] = new short[bufferSize];
            double average = 0.0;
            recorder.startRecording();
            recorder.read(data, 0, bufferSize);

            recorder.stop();
            for (short s : data) {
                if (s > 0) {
                    average += Math.abs(s);
                } else {
                    bufferSize--;
                }
            }
            double x = average / bufferSize;
            recorder.release();
            double db;
            // calculating the pascal pressure based on the idea that the max amplitude (between 0 and 32767) is
            // relative to the pressure
            double pressure = x / 51805.5336; // the value 51805.5336 can be derived from assuming that x=32767=0.6325 Pa and x=1=0.00002 Pa (the reference value)
            db = (20 * Math.log10(pressure / REFERENCE));
            if (db > 0) {
                noiseLevel = (int) db;
                displayNoiseLevel();
                if (NoiseDetectController.validateNoiseLevel(noiseLevel))
                    continueButton.setEnabled(true);
                else
                    continueButton.setEnabled(false);
            }
        } else
            requestPermission();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        handler.post(updater);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updater);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        switch (permsRequestCode) {
            case APP_PERMISSION_RECORD_AUDIO: {
                if (grantResults.length > 1) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Audio Permission is granted", Toast.LENGTH_SHORT).show();
                        detectNoiseLevel();
                    }
                }
                if ((grantResults.length == 1 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.RECORD_AUDIO) ||
                        ActivityCompat.shouldShowRequestPermissionRationale(this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE))
                    detectNoiseLevel();
                else {
                    Toast.makeText(getApplicationContext(), "Audio Permission is not granted", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This is needed to detect the surrounding noise level.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(NoiseDetectUI.this, new String[]{Manifest.permission.RECORD_AUDIO}, APP_PERMISSION_RECORD_AUDIO);
                            }
                        })
                        .create().show();
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, APP_PERMISSION_RECORD_AUDIO);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This is needed to detect the surrounding noise level.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(NoiseDetectUI.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, APP_PERMISSION_RECORD_AUDIO);
                            }
                        })
                        .create().show();
            } else
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, APP_PERMISSION_RECORD_AUDIO);
        }
    }

}