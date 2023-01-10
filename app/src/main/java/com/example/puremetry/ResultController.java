package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class ResultController {

    public static void loadMain(Context context) {
        Intent i = new Intent(context, MainUI.class);
        context.startActivity(i);
    }

    public static double[] readCalibration(Context context) {
        double[] calibrationArray = new double[HearingTestUI.calFrequencies.length];
        byte[] calibrationByteData = new byte[calibrationArray.length * 8];
        try {
            FileInputStream fis = context.openFileInput("Calibration");
            fis.read(calibrationByteData, 0, calibrationByteData.length);
            fis.close();
        } catch (IOException e) {
        }

        int counter = 0;
        for (int i = 0; i < calibrationArray.length; i++) {
            byte[] tmpByteBuffer = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmpByteBuffer[j] = calibrationByteData[counter];
                counter++;
            }
            // Convert each calibrated threshold value from byte into double
            calibrationArray[i] = ByteBuffer.wrap(tmpByteBuffer).getDouble();
        }
        return calibrationArray;
    }

    public static double[][] readTestData(String fileName, Context context) {
        byte[] resultByteData = new byte[HearingTestUI.frequencies.length * 8 + HearingTestUI.frequencies.length * 8];
        try {
            FileInputStream fis = context.openFileInput(fileName);
            fis.read(resultByteData, 0, resultByteData.length);
            fis.close();
        } catch (IOException e) {
        }

        // 0 for right, 1 for left
        double[][] testResults = new double[2][HearingTestUI.frequencies.length];

        int counter = 0;
        for (int i = 0; i < HearingTestUI.frequencies.length; i++) {
            byte[] tmpByteBuffer = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmpByteBuffer[j] = resultByteData[counter];
                counter++;
            }
            // Convert each obtained right ear threshold value from byte into double
            testResults[0][i] = ByteBuffer.wrap(tmpByteBuffer).getDouble();
        }

        for (int i = 0; i < HearingTestUI.frequencies.length; i++) {
            byte[] tmpByteBuffer = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmpByteBuffer[j] = resultByteData[counter];
                counter++;
            }
            // Convert each obtained left ear threshold value from byte into double
            testResults[1][i] = ByteBuffer.wrap(tmpByteBuffer).getDouble();
        }
        return testResults;
    }

}
