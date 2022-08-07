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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfilesListUI extends AppCompatActivity implements View.OnClickListener {

    private ListAdapter listAdapter;
    private static ArrayList<Profile> profiles;
    private int profileID;

    ActivityResultLauncher<Intent> newProfileResultLauncher = registerForActivityResult(
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
        setContentView(R.layout.activity_profiles_list_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        // Set OnClickListener for button
        ImageButton createButton = findViewById(R.id.createButton);

        createButton.setOnClickListener(this);

        // Get profiles list
        profiles = ProfilesListController.retrieveProfiles(this);

        // Initializing adapter class and passing our arraylist to it.
        listAdapter = new ListAdapter(this, profiles);

        RecyclerView profilesList = findViewById(R.id.profilesList);

        // Setting a layout manager for recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Setting layoutmanager and adapter to recycler view.
        profilesList.setLayoutManager(linearLayoutManager);
        profilesList.setAdapter(listAdapter);
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
        ProfilesListController.createProfile(this, profileID, newProfileResultLauncher);
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

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Viewholder> {

        private Context context;
        private ArrayList<Profile> profiles;

        // Constructor
        public ListAdapter(Context context, ArrayList<Profile> profiles) {
            this.context = context;
            this.profiles = profiles;
        }

        @NonNull
        @Override
        public ListAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // to inflate the layout for each item of recycler view.
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.profiles_list, parent, false);
            return new Viewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ListAdapter.Viewholder holder, int position) {
            // to set data to textview and imageview of each card layout
            Profile profile = profiles.get(position);

            int icon = (profile.getGender() == Gender.MALE) ? R.drawable.male : R.drawable.female;

            holder.genderIcon.setImageResource(icon);
            holder.nameText.setText(profile.getName());
            holder.dobText.setText(ProfilesListController.DATE_FORMAT.format(profile.getDateOfBirth()));
            holder.removeButton.setOnClickListener(new View.OnClickListener() {
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

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectProfile(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            // this method is used for showing number
            // of card items in recycler view.
            return profiles.size();
        }

        // View holder class for initializing of views
        public class Viewholder extends RecyclerView.ViewHolder {
            private ImageView genderIcon;
            private TextView nameText;
            private TextView dobText;
            private ImageButton removeButton;

            public Viewholder(@NonNull View itemView) {
                super(itemView);
                genderIcon = itemView.findViewById(R.id.genderIcon);
                nameText = itemView.findViewById(R.id.nameText);
                dobText = itemView.findViewById(R.id.dobText);
                removeButton = itemView.findViewById(R.id.removeButton);
            }
        }
    }

}