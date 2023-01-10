package com.example.puremetry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class ResultUI extends AppCompatActivity implements View.OnClickListener {

    private LineChart audiogramLineChart;
    private double[][] testResults;
    private double[] calibrationArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_ui);

        // Remove title
        getSupportActionBar().setTitle("");

        // Set OnClickListener for button
        Button returnButton = findViewById(R.id.returnButton);
        returnButton.setOnClickListener(this);

        audiogramLineChart = findViewById(R.id.audiogramLineChart);

        Intent i = getIntent();
        String result = i.getStringExtra("report");

        testResults = ResultController.readTestData(result, this);
        calibrationArray = ResultController.readCalibration(this);

        displayLineChart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnButton:
                ResultController.loadMain(this);
                break;
        }
    }

    public float scaleCbr(double cbr) {
        return (float) (Math.log10(cbr / 250) / Math.log10(2));
    }

    public float unScaleCbr(double cbr) {
        double calcVal = Math.pow(2, cbr) * 250;
        return (float) roundToMultipleOf(calcVal, 250);
    }

    private int roundToMultipleOf(double value, int multiple) {
        return (int) Math.round(value / multiple) * multiple;
    }

    private void displayLineChart() {
        audiogramLineChart.setExtraTopOffset(5);

        Description description = new Description();
        description.setText("");
        audiogramLineChart.setDescription(description);

        // Right Threshold
        ArrayList<Entry> rightEarThreshold = new ArrayList<Entry>();
        for (int i = 0; i < testResults[0].length; i++) {
            Entry dataPoint = new Entry(scaleCbr(HearingTestUI.frequencies[i]), roundToMultipleOf(testResults[0][i] - calibrationArray[i], 5));
            rightEarThreshold.add(dataPoint);
        }
        LineDataSet rightDataSet = new LineDataSet(rightEarThreshold, "Right");
        rightDataSet.setDrawValues(false);
        rightDataSet.setCircleColor(Color.RED);
        rightDataSet.setColor(Color.RED);

        // Left threshold
        ArrayList<Entry> leftEarThreshold = new ArrayList<Entry>();
        for (int i = 0; i < testResults[1].length; i++) {
            Entry dataPoint = new Entry(scaleCbr(HearingTestUI.frequencies[i]), roundToMultipleOf(testResults[1][i] - calibrationArray[i], 5));
            leftEarThreshold.add(dataPoint);
        }
        LineDataSet leftDataSet = new LineDataSet(leftEarThreshold, "Left");
        leftDataSet.setDrawValues(false);
        leftDataSet.setCircleColor(Color.BLUE);
        leftDataSet.setColor(Color.BLUE);
        leftDataSet.setDrawCircleHole(false);

        // X Axis
        XAxis xAxis = audiogramLineChart.getXAxis();
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(15);
        xAxis.setLabelCount(5);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                DecimalFormat mFormat;
                mFormat = new DecimalFormat("##0.#"); // use one decimal.
                return mFormat.format(unScaleCbr(value));
            }
        });

        // Y Axis
        YAxis leftAxis = audiogramLineChart.getAxisLeft();
        leftAxis.setAxisMinimum(-10);
        leftAxis.setAxisMaximum(120);
        leftAxis.setTextSize(15);
        leftAxis.setInverted(true);
        leftAxis.setTextColor(Color.BLACK);

        YAxis rightAxis = audiogramLineChart.getAxisRight();
        rightAxis.setAxisMinimum(-10);
        rightAxis.setAxisMaximum(120);
        rightAxis.setInverted(true);
        rightAxis.setTextSize(15);
        rightAxis.setTextColor(Color.BLACK);

        // Legends
        Legend legend = audiogramLineChart.getLegend();
        legend.setTextColor(Color.BLACK);
        legend.setTextSize(15);

        ArrayList<ILineDataSet> thresholdDataSet = new ArrayList<>();
        thresholdDataSet.add(rightDataSet);
        thresholdDataSet.add(leftDataSet);

        LineData data = new LineData(thresholdDataSet);

        audiogramLineChart.setData(data);
        audiogramLineChart.invalidate();
    }

}