package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

public class MainController{

    public static void loadProfilesList(Context context){
        Intent i = new Intent(context, ProfilesListUI.class);
        context.startActivity(i);
    }

    public static void loadReportsList(Context context){
        Intent i = new Intent(context, ReportsListUI.class);
        context.startActivity(i);
    }

}
