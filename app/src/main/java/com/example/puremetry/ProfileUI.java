package com.example.puremetry;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.puremetry.databinding.ActivityProfileUiBinding;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ProfileUI extends AppCompatActivity implements View.OnClickListener {

    private String mode;
    private int profileID;
    private String name;
    private Gender gender;
    private Date dateOfBirth;
    private String nric;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        // Set OnClickListener for button
        ImageButton maleButton = findViewById(R.id.maleButton);
        ImageButton femaleButton = findViewById(R.id.femaleButton);
        Button doneButton = findViewById(R.id.doneButton);

        maleButton.setOnClickListener(this);
        femaleButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);

        // DatePicker Dialog
        TextInputLayout birthTextLayout = findViewById(R.id.birthTextInputLayout);
        EditText birthEditText = findViewById(R.id.birthEditText);
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                birthEditText.setText(ProfilesListController.DATE_FORMAT.format(calendar.getTime()));
            }
        };
        birthTextLayout.setStartIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (birthEditText.getText().toString().length() == 10) {
                    String[] dob_split = birthEditText.getText().toString().split("/");
                    int day = Integer.parseInt(dob_split[0]);
                    int month = Integer.parseInt(dob_split[1]);
                    int year = Integer.parseInt(dob_split[2]);

                    if (day > 31)
                        day = 31;
                    if (month > 12)
                        month = 12;
                    if (year < 1900)
                        year = 1900;

                    new DatePickerDialog(ProfileUI.this, date, year, month - 1,
                            day).show();
                } else
                    new DatePickerDialog(ProfileUI.this, date, calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        // Get data from intent
        Intent i = getIntent();

        mode = i.getStringExtra("mode");
        if (mode.equals("new")) {
            profileID = i.getIntExtra("profileID", 0);
        }
        else if (mode.equals("existing")) {
            Profile profile = i.getParcelableExtra("profile");
            profileID = profile.getProfileID();
            name = profile.getName();
            gender = profile.getGender();
            dateOfBirth = profile.getDateOfBirth();
            nric = profile.getNric();

            EditText nameEditText = findViewById(R.id.nameEditText);
            EditText nricEditText = findViewById(R.id.nricEditText);

            nameEditText.setText(name);
            birthEditText.setText(ProfilesListController.DATE_FORMAT.format(dateOfBirth));
            nricEditText.setText(nric);

            if (gender == Gender.MALE)
                maleButton.setSelected(true);
            else
                femaleButton.setSelected(true);

            // Set all components non-editable
            nameEditText.setEnabled(false);
            maleButton.setEnabled(false);
            femaleButton.setEnabled(false);
            birthEditText.setEnabled(false);
            nricEditText.setEnabled(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.maleButton:
                v.setSelected(true);
                findViewById(R.id.femaleButton).setSelected(false);
                gender = Gender.MALE;
                break;
            case R.id.femaleButton:
                v.setSelected(true);
                findViewById(R.id.maleButton).setSelected(false);
                gender = Gender.FEMALE;
                break;
            case R.id.doneButton:
                name = ((EditText) findViewById(R.id.nameEditText)).getText().toString();
                nric = ((EditText) findViewById(R.id.nricEditText)).getText().toString();

                try {
                    String birthDate = ((EditText) findViewById(R.id.birthEditText)).getText().
                            toString();
                    if (birthDate.length() == 10) {
                        String[] dob_split = birthDate.split("/");
                        int day = Integer.parseInt(dob_split[0]);
                        int month = Integer.parseInt(dob_split[1]);
                        int year = Integer.parseInt(dob_split[2]);
                        if (day > 31 || month > 12 || year < 1900)
                            throw new ParseException("Invalid format", 0);
                        dateOfBirth = ProfilesListController.DATE_FORMAT.parse(birthDate);
                    } else
                        throw new ParseException("Invalid format", 0);
                } catch (java.text.ParseException e) {
                    Toast.makeText(ProfileUI.this, "Please enter the date in " +
                            "\"dd/MM/yyyy\" format", Toast.LENGTH_SHORT).show();
                }

                if (name.length() == 0 || nric.length() == 0 || gender == null ||
                        dateOfBirth == null)
                    Toast.makeText(ProfileUI.this, "Please fill in all fields.",
                            Toast.LENGTH_SHORT).show();
                else {
                    // Create new profile
                    if (mode.equals("new")) {
                        submitProfile();
                        finish();
                    }
                    else if (mode.equals("existing"))
                        startChatBot();
                }
                break;
        }
    }

    public void submitProfile() {
        Profile profile = ProfileController.createProfile(profileID, name, gender, dateOfBirth, nric);
        Intent i = new Intent();
        i.putExtra("profile", profile);
        setResult(Activity.RESULT_OK, i);
    }

    public void startChatBot() {
        ProfileController.loadChatBot(this);
    }

}