package com.example.puremetry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.activity.result.ActivityResultLauncher;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfilesListController {

    private static final String FILENAME = "ProfilesData.csv";
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static void createProfile(Context context, int profileID, ActivityResultLauncher<Intent> launcher) {
        Intent i = new Intent(context, ProfileUI.class);
        i.putExtra("mode", "new");
        i.putExtra("profileID", profileID);
        launcher.launch(i);
    }

    public static void loadProfile(Context context, Profile profile) {
        Intent i = new Intent(context, ProfileUI.class);
        i.putExtra("mode", "existing");

        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("SelectedProfile", context.MODE_PRIVATE);

        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data fetched from edittext
        myEdit.putInt("profileID", profile.getProfileID());
        myEdit.putString("name", profile.getName());
        myEdit.putString("gender", profile.getGender().name());
        myEdit.putLong("dateOfBirth", profile.getDateOfBirth().getTime());
        myEdit.putString("nric", profile.getNric());

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();

        context.startActivity(i);
    }

    public static void removeProfile(ArrayList<Profile> profiles, int index) {
        profiles.remove(index);
    }

    public static ArrayList<Profile> retrieveProfiles(Context context) {
        return readProfilesData(context);
    }

    private static ArrayList<Profile> readProfilesData(Context context) {
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (file.exists()) {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    String[] line_split = line.split(",");
                    int profileID = Integer.parseInt(line_split[0]);
                    String name = line_split[1];
                    Gender gender = Gender.valueOf(line_split[2]);
                    Date dateOfBirth = DATE_FORMAT.parse(line_split[3]);
                    String nric = line_split[4];

                    Profile profile = new Profile(profileID, name, gender, dateOfBirth, nric);
                    profiles.add(profile);
                }
            } else {
                file.createNewFile();
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return profiles;
    }

    public static void saveProfilesData(Context context, ArrayList<Profile> profiles) {
        try {
            File file = new File(context.getFilesDir(), FILENAME);
            if (!file.exists())
                file.createNewFile();
            // Generate lines to export
            StringBuilder lines = new StringBuilder();
            for (int i = 0; i < profiles.size(); i++) {
                Profile profile = profiles.get(i);
                String newLine = "" + profile.getProfileID() + "," + profile.getName() + "," +
                        profile.getGender() + "," + DATE_FORMAT.format(profile.getDateOfBirth())
                        + "," + profile.getNric() + "\n";
                lines.append(newLine);
            }
            // Export csv
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(lines.toString());
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
