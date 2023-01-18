package com.example.puremetry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class HistoryTakingUI extends AppCompatActivity implements View.OnClickListener {

    private TextView questionTextView;
    private Button firstOptionButton;
    private Button secondOptionButton;
    private Button submitButton;
    private TextInputLayout historyTextInputLayout;
    private EditText historyEditText;
    private int currentQuestionIndex = 0;
    private int currentOptionIndex = 0;
    private ArrayList<Response> responses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_taking_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        questionTextView = findViewById(R.id.questionTextView);
        historyTextInputLayout = findViewById(R.id.historyTextInputLayout);
        historyEditText = findViewById(R.id.historyEditText);

        // Initialize ArrayList
        responses = new ArrayList<Response>();

        // Set OnClickListener for button
        firstOptionButton = findViewById(R.id.firstOptionButton);
        secondOptionButton = findViewById(R.id.secondOptionButton);
        submitButton = findViewById(R.id.submitButton);

        firstOptionButton.setOnClickListener(this);
        secondOptionButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);

        displayQuestion();
    }

    @Override
    public void onClick(View v) {
        Button clickedButton = (Button) v;
        switch (clickedButton.getId()) {
            case R.id.firstOptionButton:
            case R.id.secondOptionButton:
                respondQuestion(clickedButton.getText().toString());
                break;
            case R.id.submitButton:
                String message = historyEditText.getText().toString();
                if (message.length() == 0)
                    Toast.makeText(HistoryTakingUI.this, "Please fill in the field.",
                            Toast.LENGTH_SHORT).show();
                else
                    respondQuestion(message);
                break;
        }
    }

    public void displayQuestion() {
        questionTextView.setText(HistoryQuestions.question[currentQuestionIndex]);
        if (currentQuestionIndex == 5 || currentQuestionIndex == 11) {
            historyEditText.getText().clear();
            historyTextInputLayout.setVisibility(View.VISIBLE);
            firstOptionButton.setVisibility(View.GONE);
            secondOptionButton.setVisibility(View.GONE);
            submitButton.setVisibility(View.VISIBLE);
            historyEditText.requestFocus();
        } else {
            historyTextInputLayout.setVisibility(View.GONE);
            firstOptionButton.setVisibility(View.VISIBLE);
            secondOptionButton.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.GONE);
            firstOptionButton.setText(HistoryQuestions.choices[currentOptionIndex][0]);
            secondOptionButton.setText(HistoryQuestions.choices[currentOptionIndex][1]);
        }
    }

    public void respondQuestion(String selectedOption) {
        Response response = HistoryTakingController.createResponse(questionTextView.getText().toString(), selectedOption);
        responses.add(response);
        if (HistoryTakingController.isLastQuestion(currentQuestionIndex)) {
            if (HistoryTakingController.checkUserWellness(selectedOption))
                HistoryTakingController.loadInstructions(this, responses);
            else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle("Get Well Soon")
                        .setMessage("Please come back again when you feel better.")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                firstOptionButton.setEnabled(false);
                secondOptionButton.setEnabled(false);
                firstOptionButton.setTextColor(getResources().getColor(R.color.silver));
                secondOptionButton.setTextColor(getResources().getColor(R.color.silver));
            }
        } else {
            currentQuestionIndex = HistoryTakingController.determineQuestion(currentQuestionIndex, selectedOption);
            currentOptionIndex = HistoryTakingController.determineOptions(currentQuestionIndex);
            displayQuestion();
        }
    }

}