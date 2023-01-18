package com.example.puremetry;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
    private TextView hearingLossTextView;
    private TextView recommendationTextView;
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
        hearingLossTextView = findViewById(R.id.hearingLossTextView);
        recommendationTextView = findViewById(R.id.recommendationTextView);

        Intent i = getIntent();
        String result = i.getStringExtra("report");

        testResults = ResultController.readTestData(result, this);
        calibrationArray = ResultController.readCalibration(this);

        String mode = i.getStringExtra("Action");
        String recommendation = "";
        // Select result from ReportList
        if (mode.equals("Existing")) {
            recommendation = ResultController.getRecommendation(this, result);
        }
        // Showing result after hearing test
        else {
            recommendation = ResultController.compileRecommendation(this);
            ResultController.storeRecommendation(this, result, recommendation);
        }

        displayLineChart();
        recommendationTextView.setText(recommendation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.returnButton:
                ResultController.loadReportsList(this);
                break;
        }
    }

    public float convertFrequencyToLabel(double cbr) {
        return (float) (Math.log10(cbr / 250) / Math.log10(2));
    }

    public float convertLabelToFrequency(double cbr) {
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
        ArrayList<Entry> rightEarDataPoint = new ArrayList<Entry>();
        ArrayList<Integer> rightEarThreshold = new ArrayList<Integer>();
        for (int i = 0; i < testResults[0].length; i++) {
            int threshold = roundToMultipleOf(testResults[0][i] - calibrationArray[i], 5);
            Entry dataPoint = new Entry(convertFrequencyToLabel(HearingTestUI.frequencies[i]), threshold);
            rightEarThreshold.add(threshold);
            rightEarDataPoint.add(dataPoint);
        }
        LineDataSet rightDataSet = new LineDataSet(rightEarDataPoint, "Right");
        rightDataSet.setDrawValues(false);
        rightDataSet.setCircleColor(Color.RED);
        rightDataSet.setColor(Color.RED);

        // Find hearing loss level for right ear
        int[] values;
        values = ResultController.findHearingLossRange(rightEarThreshold);
        int rightMin = values[0];
        int rightMax = values[1];
        String rightHearingLevel = ResultController.defineHearingLossForEachEar(rightMin, rightMax);

        // Left threshold
        ArrayList<Entry> leftEarDataPoint = new ArrayList<Entry>();
        ArrayList<Integer> leftEarThreshold = new ArrayList<Integer>();
        for (int i = 0; i < testResults[1].length; i++) {
            int threshold = roundToMultipleOf(testResults[1][i] - calibrationArray[i], 5);
            Entry dataPoint = new Entry(convertFrequencyToLabel(HearingTestUI.frequencies[i]), threshold);
            leftEarThreshold.add(threshold);
            leftEarDataPoint.add(dataPoint);
        }
        LineDataSet leftDataSet = new LineDataSet(leftEarDataPoint, "Left");
        leftDataSet.setDrawValues(false);
        leftDataSet.setCircleColor(Color.BLUE);
        leftDataSet.setColor(Color.BLUE);
        leftDataSet.setDrawCircleHole(false);

        // Find hearing loss level for left ear
        values = ResultController.findHearingLossRange(leftEarThreshold);
        int leftMin = values[0];
        int leftMax = values[1];
        String leftHearingLevel = ResultController.defineHearingLossForEachEar(leftMin, leftMax);

        // Update hearingLossTextView accordingly
        String hearingLossText = ResultController.defineHearingLossForBoth(rightHearingLevel, leftHearingLevel);
        hearingLossTextView.setText(hearingLossText);

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
                return mFormat.format(convertLabelToFrequency(value));
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