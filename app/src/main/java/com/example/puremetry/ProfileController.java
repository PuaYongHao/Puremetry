package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

public class ProfileController {

    public static Profile createProfile(int profileID, String name, Gender gender, Date dateOfBirth,
                                     String nric) {
        Profile profile = new Profile(profileID, name, gender, dateOfBirth, nric);
        return profile;
    }

    public static void loadChatBot(Context context){
        Intent i = new Intent(context, ChatBotUI.class);
        context.startActivity(i);
    }

}
