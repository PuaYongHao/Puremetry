package com.example.puremetry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.puremetry.databinding.ActivityProfilesListUiBinding;

import java.util.ArrayList;

public class ProfilesListUI extends AppCompatActivity implements View.OnClickListener {

    private ListAdapter listAdapter;
    private static ArrayList<Profile> profiles;
    private int profileID;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();

                        if (data != null) {
                            // Extract data
                            Profile profile = data.getParcelableExtra("profile");
                            profiles.add(profile);
                            ProfilesListController.saveProfilesData(ProfilesListUI.this, profiles);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityProfilesListUiBinding binding = ActivityProfilesListUiBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Remove title
        getSupportActionBar().setTitle("");

        // Set OnClickListener for button
        ImageButton createButton = findViewById(R.id.createButton);

        createButton.setOnClickListener(this);

        // Get profiles list
        profiles = ProfilesListController.retrieveProfiles(this);

        // Create profiles list view
        listAdapter = new ListAdapter(this, profiles);

        binding.profilesList.setAdapter(listAdapter);
        binding.profilesList.setClickable(true);
        binding.profilesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectProfile(position);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createButton:
                addProfile();
                break;
        }
    }

    public void addProfile() {
        profileID = (profiles.size() > 0) ? profiles.get(profiles.size() - 1).getProfileID() + 1 : 0;
        ProfilesListController.createProfile(this, profileID, activityResultLauncher);
    }

    public void selectProfile(int index) {
        Profile profile = profiles.get(index);
        ProfilesListController.loadProfile(this, profile);
    }

    public void deleteProfile(int index) {
        ProfilesListController.removeProfile(profiles, index);
        ProfilesListController.saveProfilesData(this, profiles);
        listAdapter.notifyDataSetChanged();
    }

    private class ListAdapter extends ArrayAdapter<Profile> {

        public ListAdapter(Context context, ArrayList<Profile> profiles) {
            super(context, R.layout.profiles_list, profiles);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Profile profile = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.profiles_list, parent, false);
            }

            ImageView genderIcon = convertView.findViewById(R.id.genderIcon);
            TextView nameText = convertView.findViewById(R.id.nameText);
            TextView dobText = convertView.findViewById(R.id.dobText);
            ImageButton removeButton = convertView.findViewById(R.id.removeButton);

            int icon = (profile.getGender() == Gender.MALE) ? R.drawable.male : R.drawable.female;

            genderIcon.setImageResource(icon);
            nameText.setText(profile.getName());
            dobText.setText(ProfilesListController.DATE_FORMAT.format(profile.getDateOfBirth()));
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProfilesListUI.this);

                    alertDialogBuilder.setTitle("Are you sure you want to delete?")
                            .setMessage("This profile will be deleted immediately. You can't undo this action.")
                            .setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteProfile(position);
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            }).show();
                }
            });
            return convertView;
        }
    }

}