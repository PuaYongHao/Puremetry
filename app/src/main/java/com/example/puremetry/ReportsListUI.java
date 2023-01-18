package com.example.puremetry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.util.ArrayList;

public class ReportsListUI extends AppCompatActivity {

    private ListAdapter listAdapter;
    private static ArrayList<String> reports;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_list_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        // Get report list
        reports = ReportsListController.retrieveReports(this);

        // Initializing adapter class and passing our arraylist to it.
        listAdapter = new ListAdapter(reports);

        RecyclerView reportsList = findViewById(R.id.reportsList);

        // Setting a layout manager for recycler view.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        // Setting layoutmanager and adapter to recycler view.
        reportsList.setLayoutManager(linearLayoutManager);
        reportsList.setAdapter(listAdapter);
    }

    public void deleteReport(int index) {
        ReportsListController.removeReport(this, reports, index);
        listAdapter.notifyDataSetChanged();
    }

    public void selectReport(int index) {
        String report = reports.get(index);
        ReportsListController.loadResult(this, report);
    }

    public class ListAdapter extends RecyclerView.Adapter<ListAdapter.Viewholder> {

        private ArrayList<String> reports;

        // Constructor
        public ListAdapter(ArrayList<String> reports) {
            this.reports = reports;
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
            String report = reports.get(position);

            int icon = R.drawable.result;
            holder.icon.setImageResource(icon);

            String[] names = report.split("-");
            String time = DateFormat.getTimeInstance(DateFormat.SHORT).format(Long.parseLong(names[1])) + ", " + DateFormat.getDateInstance(DateFormat.SHORT).format(Long.parseLong(names[1]));
            holder.nameText.setText(names[0]);
            holder.dateText.setText(time);

            holder.removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportsListUI.this);

                    alertDialogBuilder.setTitle("Are you sure you want to delete?")
                            .setMessage("This report will be deleted immediately. You can't undo this action.")
                            .setCancelable(false)
                            .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    deleteReport(position);
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
                    selectReport(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            // this method is used for showing number
            // of card items in recycler view.
            return reports.size();
        }

        // View holder class for initializing of views
        public class Viewholder extends RecyclerView.ViewHolder {
            private ImageView icon;
            private TextView nameText;
            private TextView dateText;
            private ImageButton removeButton;

            public Viewholder(@NonNull View itemView) {
                super(itemView);
                icon = itemView.findViewById(R.id.genderIcon);
                nameText = itemView.findViewById(R.id.nameText);
                dateText = itemView.findViewById(R.id.dobText);
                removeButton = itemView.findViewById(R.id.removeButton);
            }
        }
    }

}