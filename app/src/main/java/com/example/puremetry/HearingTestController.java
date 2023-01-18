package com.example.puremetry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HearingTestController {

    private static String fileName;

    public static int randomTime() {
        // Generate a time interval of 2 to 3 seconds between beeps
        double num = Math.random();
        return (int) (2000 + 1000 * num);
    }

    public static boolean isCalibrated(Context context) {
        List<String> list = new ArrayList<String>(Arrays.asList(context.fileList()));
        return list.contains("Calibration");
    }

    public static void writeCalibration(double[] calibrationArray, Context context) {
        int counter = 0;
        byte[] calibrationByteArray = new byte[calibrationArray.length * 8];
        for (int x = 0; x < calibrationArray.length; x++) {
            byte[] tmpByteArray = new byte[8];
            // Convert each calibrated threshold value from double into byte
            ByteBuffer.wrap(tmpByteArray).putDouble(calibrationArray[x]);
            for (int j = 0; j < 8; j++) {
                calibrationByteArray[counter] = tmpByteArray[j];
                counter++;
            }
        }
        try {
            FileOutputStream fileOutputStream = context.openFileOutput("Calibration", Context.MODE_PRIVATE);
            try {
                fileOutputStream.write(calibrationByteArray);
                fileOutputStream.close();
            } catch (IOException q) {
                System.out.println(q.toString());
            }
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        }
    }

    public static void writeTestResult(double[] thresholds_right, double[] thresholds_left, Context context) {
        int counter;

        SharedPreferences sh = context.getSharedPreferences("SelectedProfile", Context.MODE_PRIVATE);
        String user = sh.getString("name", "");

        String currentDateTime = Long.toString(System.currentTimeMillis());

        fileName = user + "-" + currentDateTime;

        counter = 0;

        byte[] thresholdVolume = new byte[thresholds_right.length * 8 + thresholds_left.length * 8];

        for (double v : thresholds_right) {
            byte[] tmpByteArray = new byte[8];
            // Convert each threshold value from double into byte
            ByteBuffer.wrap(tmpByteArray).putDouble(v);
            for (int j = 0; j < 8; j++) {
                thresholdVolume[counter] = tmpByteArray[j];
                counter++;
            }
        }
        for (double v : thresholds_left) {
            byte[] tmpByteArray = new byte[8];
            // Convert each threshold value from double into byte
            ByteBuffer.wrap(tmpByteArray).putDouble(v);
            for (int j = 0; j < 8; j++) {
                thresholdVolume[counter] = tmpByteArray[j];
                counter++;
            }
        }

        try {
            FileOutputStream fileOutputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            try {
                fileOutputStream.write(thresholdVolume);
                fileOutputStream.close();
            } catch (IOException q) {
            }
        } catch (FileNotFoundException e) {
        }
    }

    // Generates tone based on the increment and volume
    public static float[] generateTone(float increment, int volume, int numSamples) {
        float angle = 0;
        float[] generatedSnd = new float[numSamples];
        for (int i = 0; i < numSamples; i++) {
            generatedSnd[i] = (float) (Math.sin(angle) * volume / 32768);
            angle += increment;
        }
        return generatedSnd;
    }

    // Writes the parameter byte array to an AudioTrack and plays the array
    public static AudioTrack playSound(float[] generatedSnd, int ear, int sampleRate) {
        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_FLOAT, generatedSnd.length, AudioTrack.MODE_STATIC);
        audioTrack.write(generatedSnd, 0, generatedSnd.length, AudioTrack.WRITE_BLOCKING);
        if (ear == 0) {
            audioTrack.setStereoVolume(0, AudioTrack.getMaxVolume());
        } else {
            audioTrack.setStereoVolume(AudioTrack.getMaxVolume(), 0);
        }
        audioTrack.play();
        return audioTrack;
    }

    public static void loadMain(Context context) {
        Intent i = new Intent(context, MainUI.class);
        context.startActivity(i);
    }

    public static void loadResult(Context context) {
        Intent i = new Intent(context, ResultUI.class);
        i.putExtra("report", fileName);
        i.putExtra("Action", "New");
        context.startActivity(i);
    }

}
