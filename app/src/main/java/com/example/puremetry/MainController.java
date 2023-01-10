package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainController {

    public static void loadProfilesList(Context context) {
        Intent i = new Intent(context, ProfilesListUI.class);
        context.startActivity(i);
    }

    public static void loadReportsList(Context context) {
        Intent i = new Intent(context, ReportsListUI.class);
        context.startActivity(i);
    }

    public static void loadInstructions(Context context) {
        Intent i = new Intent(context, InstructionsUI.class);
        i.putExtra("Action", "Calibration");
        context.startActivity(i);
    }

}
