package com.example.puremetry;

import android.content.Context;
import android.content.Intent;

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

    public static void loadInstructions(Context context) {
        Intent i = new Intent(context, InstructionsUI.class);
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
