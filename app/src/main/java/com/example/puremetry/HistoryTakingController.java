package com.example.puremetry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;

public class HistoryTakingController {

    public static int determineQuestion(int currentQuestionIndex, String selectedOption) {
        if (currentQuestionIndex == 2 && selectedOption.equals("No"))
            currentQuestionIndex = 7;
        else if (currentQuestionIndex == 3 && selectedOption.equals("Both"))
            currentQuestionIndex = 5;
        else if (currentQuestionIndex == 9 && selectedOption.equals("No"))
            currentQuestionIndex = 12;
        else if (currentQuestionIndex == 10 && selectedOption.equals("No"))
            currentQuestionIndex = 12;
        else
            currentQuestionIndex++;
        return currentQuestionIndex;
    }

    public static int determineOptions(int currentQuestionIndex) {
        int currentOptionIndex;
        if (currentQuestionIndex == 3)
            currentOptionIndex = 1;
        else if (currentQuestionIndex == 4)
            currentOptionIndex = 2;
        else if (currentQuestionIndex == 6)
            currentOptionIndex = 3;
        else
            currentOptionIndex = 0;
        return currentOptionIndex;
    }

    public static Response createResponse(String question, String message) {
        Response response = new Response(question, message);
        return response;
    }

    public static void loadInstructions(Context context, ArrayList<Response> responses) {
        Intent i = new Intent(context, InstructionsUI.class);
        i.putExtra("Action", "Test");

        // Storing data into SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("ClinicalHistory", context.MODE_PRIVATE);

        Gson gson = new Gson();
        String json = gson.toJson(responses);

        // Creating an Editor object to edit(write to the file)
        SharedPreferences.Editor myEdit = sharedPreferences.edit();

        // Storing the key and its value as the data
        myEdit.remove("responses").commit();
        myEdit.putString("responses", json);

        // Once the changes have been made,
        // we need to commit to apply those changes made,
        // otherwise, it will throw an error
        myEdit.commit();

        context.startActivity(i);
    }

    public static boolean isLastQuestion(int currentQuestionIndex) {
        if (currentQuestionIndex == HistoryQuestions.question.length - 1)
            return true;
        return false;
    }

    public static boolean checkUserWellness(String selectedOption) {
        if (selectedOption.equals("Yes"))
            return true;
        return false;
    }

}
