package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

public class MainController {

    public static void loadProfilesList(Context context) {
        Intent i = new Intent(context, ProfilesListUI.class);
        context.startActivity(i);
    }

    public static void loadResultsList(Context context) {
        Intent i = new Intent(context, ResultsListUI.class);
        context.startActivity(i);
    }

    public static void loadInstructions(Context context) {
        Intent i = new Intent(context, InstructionsUI.class);
        i.putExtra("Action", "Calibration");
        context.startActivity(i);
    }

}
