package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReportsListController {

    public static ArrayList<String> retrieveReports(Context context) {
        ArrayList<String> reports = new ArrayList<String>(Arrays.asList(context.fileList()));
        Collections.sort(reports, Collections.reverseOrder());
        for (Iterator<String> iterator = reports.iterator(); iterator.hasNext(); ) {
            String string = iterator.next();
            Pattern pattern = Pattern.compile("^[A-Za-z0-9]+-\\d+");
            Matcher matcher = pattern.matcher(string);
            if (!matcher.find())
                iterator.remove();
        }
        return reports;
    }

    public static void removeReport(Context context, ArrayList<String> reports, int index) {
        String fileName = reports.get(index);
        reports.remove(index);
        File file = new File(context.getFilesDir() + "/" + fileName);
        file.delete();
        File recommendationFile = new File(context.getFilesDir() + "/Recommendation-" + fileName);
        recommendationFile.delete();
    }

    public static void loadResult(Context context, String report) {
        Intent i = new Intent(context, ResultUI.class);
        i.putExtra("report", report);
        i.putExtra("Action", "Existing");
        context.startActivity(i);
    }

}
