package com.example.puremetry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

public class HearingTestUI extends AppCompatActivity implements View.OnClickListener {
    private final int DURATION = 1;
    private final int SAMPLE_RATE = 44100;
    private final int NUM_SAMPLES = DURATION * SAMPLE_RATE;
    private final int VOLUME = 32767;
    public static final int[] CAL_FREQUENCIES = {250, 500, 1000, 2000, 4000, 8000};
    public static final int[] FREQUENCIES = {250, 500, 1000, 2000, 4000, 8000};
    public double[] thresholds_right;
    public double[] thresholds_left;
    public static int gain = 9;
    private boolean heard = false;
    private testThread testThread;
    private Intent intent;

    private TextView frequencyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hearing_test_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        frequencyTextView = findViewById(R.id.frequencyTextView);

        // Set OnClickListener for button
        Button responseButton = findViewById(R.id.responseButton);
        responseButton.setOnClickListener(this);

        intent = getIntent();

        if (intent.getStringExtra("Action").equals("Calibration")) {
            thresholds_right = new double[CAL_FREQUENCIES.length];
            thresholds_left = new double[CAL_FREQUENCIES.length];
        } else {
            thresholds_right = new double[FREQUENCIES.length];
            thresholds_left = new double[FREQUENCIES.length];
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.responseButton:
                heard = true;
                Timer timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                    }
                };
                timer.schedule(timerTask, 1000);
                break;
        }
    }

    @Override
    public void onResume() {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);
        am.setStreamVolume(AudioManager.STREAM_MUSIC, gain, 0);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Ready?")
                .setMessage("Let's get started")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        testThread = new testThread();
                        testThread.start();
                        dialog.cancel();
                    }
                }).show();
        super.onResume();
    }

    @Override
    public void onStop() {
        testThread.stopThread();
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
                || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            return true;
        else
            return false;
    }

    public class testThread extends Thread {
        private boolean stopped = false;

        public void stopThread() {
            stopped = true;
        }

        public void run() {
            // for both left and right ears, 0 @ right and 1 @ left
            for (int s = 0; s < 2; s++) {
                if (stopped) break;

                if (intent.getStringExtra("Action").equals("Calibration")) {
                    // iterated once for every frequency to be calibrated
                    for (int i = 0; i < CAL_FREQUENCIES.length; i++) {
                        obtainThreshold(s, i);
                    }
                } else {
                    // iterated once for every frequency to be tested
                    for (int i = 0; i < FREQUENCIES.length; i++) {
                        obtainThreshold(s, i);
                    }
                }
            }
            if (stopped) return;

            if (!intent.getStringExtra("Action").equals("Test")) {  // store calibration
                double[] calibrationArray = new double[CAL_FREQUENCIES.length];
                for (int i = 0; i < CAL_FREQUENCIES.length; i++) {  // average left/right channels
                    calibrationArray[i] = (thresholds_left[i] + thresholds_right[i]) / 2;
                }
                HearingTestController.writeCalibration(calibrationArray, HearingTestUI.this);
                HearingTestController.loadMain(HearingTestUI.this);
            } else {  // store test result
                HearingTestController.writeTestResult(thresholds_right, thresholds_left, HearingTestUI.this);
                HearingTestController.loadResult(HearingTestUI.this);
            }
        }

        private void obtainThreshold(int ear, int frequencyIndex) {
            double threshold = hearingTest(ear, frequencyIndex);
            //records volume as threshold
            if (ear == 0) {
                thresholds_right[frequencyIndex] = threshold;
            } else {
                thresholds_left[frequencyIndex] = threshold;
            }
        }

        private double hearingTest(int ear, int frequencyIndex) {
            AudioTrack audioTrack;

            int frequency;
            if (intent.getStringExtra("Action").equals("Calibration"))
                frequency = CAL_FREQUENCIES[frequencyIndex];
            else
                frequency = FREQUENCIES[frequencyIndex];

            if (ear == 0)
                frequencyTextView.setText("Right " + frequency);
            else
                frequencyTextView.setText("Left " + frequency);

            float increment = (float) (2 * Math.PI) * frequency / SAMPLE_RATE;
            int actualVolume;
            int maxVolume = VOLUME;
            int minVolume = 1;
            while (!stopped) {
                int response = 0;

                // Adjust tone volume
                if (minVolume > 1) {
                    // In a slower manner after a tone is not heard
                    actualVolume = (minVolume + maxVolume) / 2;
                } else {
                    // In a faster manner until tone is not heard for the first time
                    actualVolume = (maxVolume) / 3;
                }

                if (actualVolume <= 1) {
                    actualVolume = 1;
                }

                // Return threshold value if difference between min and max volume is less than 3dB
                if (((float) maxVolume / (float) minVolume) < Math.sqrt(2)) {
                    return 20 * Math.log10(maxVolume);
                } else {
                    //iterate same tone three times
                    for (int z = 0; z < 3; z++) {
                        if (stopped) break;

                        heard = false;
                        audioTrack = HearingTestController.playSound(HearingTestController.generateTone(increment, actualVolume, NUM_SAMPLES), ear, SAMPLE_RATE);
                        try {
                            Thread.sleep(HearingTestController.randomTime());
                        } catch (InterruptedException e) {
                        }
                        audioTrack.release();

                        if (heard) response++;

                        // Skip if the first two test were positive
                        if (response >= 2) break;

                        // Skip if the first two tests were misses
                        if (z == 1 && response == 0) break;
                    }
                    // Reduce maxVolume if there are at least 2 responses
                    if (response >= 2) {
                        maxVolume = actualVolume;
                    } else {
                        // Increase minVolume when a tone is not heard
                        if (minVolume > 1) {
                            minVolume = actualVolume;
                        } else {
                            // Set minVolume to be 3dB below actualVolume when a tone is not heard for first time
                            minVolume = (int) (actualVolume / Math.sqrt(2));
                        }
                    }
                } //continue with test
            }
            return 0;
        }
    }

}
