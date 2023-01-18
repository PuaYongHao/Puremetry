package com.example.puremetry;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class NoiseDetectController extends Activity {

    private static String[] noise_description = {
            "This is more likely to damage your hearing over time.",
            "Find a quiet place.",
            "It is good to go."
    };

    public static boolean checkPermission(Context context) {
        int result = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int result2 = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED;
    }

    public static void loadHearingTest(Context context) {
        Intent i = new Intent(context, HearingTestUI.class);
        i.putExtra("Action","Test");
        context.startActivity(i);
    }

    public static void loadCalHearingTest(Context context) {
        Intent i = new Intent(context, HearingTestUI.class);
        i.putExtra("Action","Calibration");
        context.startActivity(i);
    }

    public static Boolean validateNoiseLevel(int noiseLevel) {
        if (noiseLevel <= 40)
            return true;
        return false;
    }

    public static String updateDescription(int noiseLevel) {
        if (noiseLevel >= 70)
            return noise_description[0];
        else if (noiseLevel <= 40)
            return noise_description[2];
        else
            return noise_description[1];
    }

}
