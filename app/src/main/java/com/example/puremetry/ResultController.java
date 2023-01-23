package com.example.puremetry;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultController {

    private static String ear = "";
    private static String sinceWhen = "";
    private static String twoSymptoms = "F";

    public static void loadResultsList(Context context) {
        Intent i = new Intent(context, ResultsListUI.class);
        context.startActivity(i);
    }

    public static double[] readCalibration(Context context) {
        double[] calibrationArray = new double[HearingTestUI.CAL_FREQUENCIES.length];
        byte[] calibrationByteData = new byte[calibrationArray.length * 8];
        try {
            FileInputStream fis = context.openFileInput("Calibration");
            fis.read(calibrationByteData, 0, calibrationByteData.length);
            fis.close();
        } catch (IOException e) {
        }

        int counter = 0;
        for (int i = 0; i < calibrationArray.length; i++) {
            byte[] tmpByteBuffer = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmpByteBuffer[j] = calibrationByteData[counter];
                counter++;
            }
            // Convert each calibrated threshold value from byte into double
            calibrationArray[i] = ByteBuffer.wrap(tmpByteBuffer).getDouble();
        }
        return calibrationArray;
    }

    public static double[][] readTestData(String fileName, Context context) {
        byte[] resultByteData = new byte[HearingTestUI.FREQUENCIES.length * 8 + HearingTestUI.FREQUENCIES.length * 8];
        try {
            FileInputStream fis = context.openFileInput(fileName);
            fis.read(resultByteData, 0, resultByteData.length);
            fis.close();
        } catch (IOException e) {
        }

        // 0 for right, 1 for left
        double[][] testResults = new double[2][HearingTestUI.FREQUENCIES.length];

        int counter = 0;
        for (int i = 0; i < HearingTestUI.FREQUENCIES.length; i++) {
            byte[] tmpByteBuffer = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmpByteBuffer[j] = resultByteData[counter];
                counter++;
            }
            // Convert each obtained right ear threshold value from byte into double
            testResults[0][i] = ByteBuffer.wrap(tmpByteBuffer).getDouble();
        }

        for (int i = 0; i < HearingTestUI.FREQUENCIES.length; i++) {
            byte[] tmpByteBuffer = new byte[8];
            for (int j = 0; j < 8; j++) {
                tmpByteBuffer[j] = resultByteData[counter];
                counter++;
            }
            // Convert each obtained left ear threshold value from byte into double
            testResults[1][i] = ByteBuffer.wrap(tmpByteBuffer).getDouble();
        }
        return testResults;
    }

    public static int[] findHearingLossRange(ArrayList<Integer> thresholds) {
        int[] values = new int[2];
        values[0] = 120; // min
        values[1] = 25; // max
        for (int i = 0; i < thresholds.size(); i++) {
            if (thresholds.get(i) > 25 && thresholds.get(i) < values[0]) {
                values[0] = thresholds.get(i);
            }
            if (thresholds.get(i) > values[1]) {
                values[1] = thresholds.get(i);
            }
        }
        return values;
    }

    public static String defineHearingLossForEachEar(int min, int max) {
        if (min == 120 && max == 25)
            return "normal hearing ";
        else {
            String minDescription = categoriseHearingLoss(min);
            String maxDescription = categoriseHearingLoss(max);
            if (minDescription != maxDescription)
                return minDescription + "to " + maxDescription + "hearing loss ";
            else
                return minDescription + "hearing loss ";
        }
    }

    private static String categoriseHearingLoss(int hearingThreshold) {
        if (hearingThreshold >= 26 && hearingThreshold <= 40)
            return "mild ";
        else if (hearingThreshold >= 41 && hearingThreshold <= 55)
            return "moderate ";
        else if (hearingThreshold >= 56 && hearingThreshold <= 70)
            return "moderately severe ";
        else if (hearingThreshold >= 71 && hearingThreshold <= 90)
            return "severe ";
        else
            return "profound ";
    }

    public static String defineHearingLossForBoth(String rightEarDescription, String leftEarDescription) {
        // Bilaterally
        if (Objects.equals(rightEarDescription, leftEarDescription)) {
            if (Objects.equals(rightEarDescription, "normal hearing"))
                return "This shows that you have normal hearing bilaterally.";
            else
                return "This shows that you have " + rightEarDescription + "bilaterally.";
        }
        // Unilaterally
        else {
            return "This shows that you have " + rightEarDescription + "on the right and " + leftEarDescription + "on the left.";
        }
    }

    public static String compileRecommendation(Context context) {
        Gson gson = new Gson();

        // Fetching the stored data from the SharedPreference
        SharedPreferences sh = context.getSharedPreferences("ClinicalHistory", context.MODE_PRIVATE);

        String clinicalHistory = sh.getString("responses", "");
        ArrayList<Response> responses = gson.fromJson(clinicalHistory,
                new TypeToken<List<Response>>() {
                }.getType());

        // Get Pronoun
        SharedPreferences sharedPreferences = context.getSharedPreferences("SelectedProfile", context.MODE_PRIVATE);
        Gender gender = Gender.valueOf(sharedPreferences.getString("gender", "MALE"));
        String pronoun = "";
        if (gender == Gender.MALE)
            pronoun = "he";
        else
            pronoun = "she";

        String recommendation = "";
        for (int i = 0; i < responses.size(); i++) {
            Response response = responses.get(i);
            String newLine = generateRecommendation(response, pronoun);
            if (newLine.equals(""))
                continue;
            recommendation += newLine + " ";
        }
        return recommendation;
    }

    private static String generateRecommendation(Response response, String pronoun) {
        if (Objects.equals(response.getQuestion(), HistoryQuestions.question[0]) &&
                Objects.equals(response.getMessage(), "Yes"))
            return "Due to your history of noise exposure, 3kHz and 6kHz should be tested.";
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[1]) &&
                Objects.equals(response.getMessage(), "Yes"))
            return "As you have family history of hearing loss, test results may be complicated.";
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[3]))
            ear = response.getMessage();
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[4]))
            ear = response.getMessage();
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[5]))
            sinceWhen = response.getMessage();
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[6])) {
            String experience = response.getMessage();
            if (Objects.equals(ear, "Both"))
                return "Note that client feels " + pronoun + " has difficulty in hearing for both " +
                        "ears since " + sinceWhen.toLowerCase() + " " + experience.toLowerCase() + ".";
            else
                return "Note that client feels " + pronoun + " has difficulty in hearing for " +
                        ear.toLowerCase() + " ear since " + sinceWhen.toLowerCase() + " " + experience.toLowerCase() + ".";
        } else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[7]) &&
                Objects.equals(response.getMessage(), "Yes"))
            return "As you have ringing in your ears, this suggest some hearing loss.";
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[10]) &&
                Objects.equals(response.getMessage(), "No"))
            // Seen doctor but did not have any operation
            return "This requires follow-up since you have seen ENT before.";
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[11])) {
            // Seen doctor and had operation
            String operation = response.getMessage();
            return "As you have undergone " + operation + ", test results may be complicated and " +
                    "require follow-up.";
        } else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[12]) &&
                Objects.equals(response.getMessage(), "Yes"))
            twoSymptoms = "T";
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[13]) &&
                Objects.equals(response.getMessage(), "Yes"))
            twoSymptoms += "T";
        else if (Objects.equals(response.getQuestion(), HistoryQuestions.question[13]) &&
                Objects.equals(response.getMessage(), "No"))
            twoSymptoms += "F";

        if (Objects.equals(response.getQuestion(), HistoryQuestions.question[13])) {
            if (Objects.equals(twoSymptoms, "TT"))
                return "Please refer to doctor to rule out any other medical problems due to your " +
                        "numbness in face and episodes of dizziness.";
            else if (Objects.equals(twoSymptoms, "TF"))
                return "Please refer to doctor to rule out any other medical problems due to your " +
                        "numbness in face.";
            else if (Objects.equals(twoSymptoms, "FT"))
                return "Please refer to doctor to rule out any other medical problems due to your " +
                        "episodes of dizziness.";
        }

        return "";
    }

    public static String getRecommendation(Context context, String fileName) {
        String[] names = fileName.split("-");
        String time = names[1];

        String recommendationFileName = "Recommendation" + "-" + names[0] + "-" + time;
        String recommendation = "";
        try {
            InputStream inputStream = context.openFileInput(recommendationFileName);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }
                inputStream.close();
                recommendation = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return recommendation;
    }

    public static void storeRecommendation(Context context, String fileName, String recommendation) {
        String[] names = fileName.split("-");
        String time = names[1];

        String recommendationFileName = "Recommendation" + "-" + names[0] + "-" + time;
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(recommendationFileName, context.MODE_PRIVATE));
            outputStreamWriter.write(recommendation);
            outputStreamWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
