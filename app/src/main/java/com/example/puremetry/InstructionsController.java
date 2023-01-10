package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

public class InstructionsController {

    public static void loadNoiseDetect(Context context) {
        Intent i = new Intent(context, NoiseDetectUI.class);
        i.putExtra("Action", "Test");
        context.startActivity(i);
    }

    public static void loadCalNoiseDetect(Context context) {
        Intent i = new Intent(context, NoiseDetectUI.class);
        i.putExtra("Action", "Calibration");
        context.startActivity(i);
    }

}
