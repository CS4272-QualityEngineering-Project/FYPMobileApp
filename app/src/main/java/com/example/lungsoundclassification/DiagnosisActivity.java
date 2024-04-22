package com.example.lungsoundclassification;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class DiagnosisActivity extends AppCompatActivity {

    private List<DiagnosisModel> diagnosisList;
    private List<DiagnosisModel> viewableDiagnosisList;
    private DiagnosisAdapter adapter;

    private CardView healthyCard;
    private CardView diagnosisCard;
    private CardView radarChartCard;
    private TextView healthyDisclaimer;

    // TODO: TEST
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagnosis_view);

        // getting extra
        ResponseObject responseObject = (ResponseObject) getIntent().getSerializableExtra("response_object");

        initializeViews();

        if (responseObject != null) {
            if (responseObject.getDiseases().isEmpty()) {
                handleHealthyCase();
            } else {
                handleDiagnosisCase(responseObject);
            }
        }
    }

    private void initializeViews() {
        healthyCard = findViewById(R.id.healthy_card);
        diagnosisCard = findViewById(R.id.diagnosis_card);
        radarChartCard = findViewById(R.id.radar_chart_card);
        healthyDisclaimer = findViewById(R.id.healthy_description);
    }

    private void handleHealthyCase() {
        String updatedPercentage = "85.12";  // Replace with your updated percentage value
        String updatedText = getString(R.string.disclaimer_health_1, updatedPercentage);
        healthyDisclaimer.setText(updatedText);

        healthyCard.setVisibility(View.VISIBLE);
        diagnosisCard.setVisibility(View.GONE);
        radarChartCard.setVisibility(View.GONE);
    }

    private void handleDiagnosisCase(ResponseObject responseObject) {
        healthyCard.setVisibility(View.GONE);
        diagnosisCard.setVisibility(View.VISIBLE);
        radarChartCard.setVisibility(View.VISIBLE);

        setupRadarChart(responseObject);
        setupRecyclerView(responseObject);
        setupExpandButton();
    }

    private void setupRadarChart(ResponseObject responseObject) {
        RadarChart radarChart = findViewById(R.id.radarChart);

        List<Float> probabilities = responseObject.getProbabilities();
        List<RadarEntry> entries = new ArrayList<>();
        entries.add(new RadarEntry(probabilities.get(0) * 100));
        entries.add(new RadarEntry(probabilities.get(1) * 100));
        entries.add(new RadarEntry(probabilities.get(2) * 100));

        RadarDataSet dataSet = new RadarDataSet(entries, "Label");
        dataSet.setColor(Color.RED);
        dataSet.setFillColor(Color.RED);
        dataSet.setDrawFilled(true);

        radarChart.getLegend().setEnabled(false); // Remove the description (legend)
        radarChart.setExtraOffsets(0, 0, 0, 0);
        radarChart.setRotationEnabled(false);

        RadarData data = new RadarData(dataSet);
        radarChart.setData(data);
        radarChart.getDescription().setEnabled(false);

        YAxis yAxis = radarChart.getYAxis();
        yAxis.setAxisMaximum(90f); // Set maximum value to 100
        yAxis.setGranularity(10f); // Set granularity to 10

        dataSet.setDrawValues(false);

        // Customize the X axis labels
        XAxis xAxis = radarChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(responseObject.getDiseases())); // Set custom labels

        radarChart.invalidate(); // Refresh the chart
    }

    private void setupRecyclerView(ResponseObject responseObject) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        DefaultItemAnimator animator = new DefaultItemAnimator();
        animator.setAddDuration(200);
        animator.setRemoveDuration(200);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(animator);

        List<DiagnosisModel> diagnosisList = getDiagnosisData(responseObject.getDiseases(), responseObject.getProbabilities()); // Replace with your data source
        adapter = new DiagnosisAdapter(diagnosisList);
        recyclerView.setAdapter(adapter);
    }

    private void setupExpandButton() {
        TextView expand_btn = findViewById(R.id.diagnosis_seemore);

        expand_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle button click
                if(expand_btn.getText().equals(getString(R.string.show_more))){
                    expandList();
                    expand_btn.setText(getString(R.string.show_less));

                } else {
                    minimizeList();
                    expand_btn.setText(getString(R.string.show_more));
                }
                adapter.notifyDataSetChanged();
            }
        });
    }

    // Replace this method with your actual data source logic
    // TODO: TEST
    protected List<DiagnosisModel> getDiagnosisData(List<String> diagnosisNames, List<Float> diagnosisProbs) {
        List<DiagnosisModel> newDiagnosisList = new ArrayList<>();
        for (int i = 0; i < diagnosisNames.size(); i++) {
            System.out.println(diagnosisNames.get(i) + " " + diagnosisProbs.get(i));
            newDiagnosisList.add(new DiagnosisModel(diagnosisNames.get(i), String.format("%.2f%%", diagnosisProbs.get(i) * 100)));
        }

        setDiagnosisList(newDiagnosisList);

        List<DiagnosisModel> newViewableDiagnosisList = new ArrayList<>();
        newViewableDiagnosisList.add(diagnosisList.get(0));

        setViewableDiagnosisList(newViewableDiagnosisList);

        return viewableDiagnosisList;
    }


    // TODO: TEST
    private void expandList(){
        for(int i = 1; i < diagnosisList.size(); i++){
            viewableDiagnosisList.add(i, diagnosisList.get(i));
            adapter.notifyItemInserted(i);
        }

    }

    // TODO: TEST
    private void minimizeList(){
        for(int i = diagnosisList.size() - 1; i >= 1; i--){
            viewableDiagnosisList.remove(i);
            adapter.notifyItemRemoved(i);
        }
    }

    public void setDiagnosisList(List<DiagnosisModel> diagnosisList) {
        this.diagnosisList = diagnosisList;
    }

    public void setViewableDiagnosisList(List<DiagnosisModel> viewableDiagnosisList) {
        this.viewableDiagnosisList = viewableDiagnosisList;
    }

}


//
//    private int calculateSeverity(List<Integer> severityValues){
//        int severity_sum = 0;
//        for (int severityValue:severityValues) {
//            severity_sum += severityValue;
//        }
//
//        return severity_sum / severityValues.size();
//    }
//
//    private void setSeverityLevel(int severity) {
//        int progressValue;
//
//        switch (severity) {
//            case 4:
//                progressValue = 100;
//                severityImpression.setImageResource(R.drawable.sad_plus);
//                break;
//            case 3:
//                progressValue = 75;
//                severityImpression.setImageResource(R.drawable.sad);
//                break;
//            case 2:
//                progressValue = 50;
//                severityImpression.setImageResource(R.drawable.neutral);
//                break;
//            case 1:
//                progressValue = 25;
//                severityImpression.setImageResource(R.drawable.happy);
//                break;
//            case 0:
//                progressValue = 0;
//                severityImpression.setImageResource(R.drawable.happy_plus);
//                break;
//            default:
//                progressValue = 0;
//                break;
//        }
//
//        animateProgressBar(progressValue);
//    }

//    private void animateProgressBar(final int targetProgress) {
//
//        final int steps = 25; // You can adjust the animation speed by changing the number of steps
//        final int delay = 20; // Delay between each step in milliseconds
//
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.post(new Runnable() {
//
//            int currentProgress = severityProgressBar.getProgress();
//            final int diff = targetProgress - currentProgress;
//            final float step = (float) diff / steps;
//
//            @Override
//            public void run() {
//                if (currentProgress != targetProgress) {
//
//                    int color = calculateColor(currentProgress);
//                    severityProgressBar.setProgressTintList(ColorStateList.valueOf(color));
//
//                    severityProgressBar.setProgress(currentProgress + Math.round(step));
//                    currentProgress = Math.round(currentProgress + step);
//
//
//                    handler.postDelayed(this, delay);
//                }
//            }
//        });
//    }

//    private int calculateColor(int progress) {
////        int green = (int) Math.max(((150 - progress) * 2.55), 0); // 0% -> Green
////        int red = (int) (progress * 2.55); // 100% -> Red
//
//        int green, red;
//
//        if (progress < 50){
//            green = 255;
//            red = (int) (progress * 2.55 * 2);
//        }
//
//        else{
//            red = 255;
//            green = (int)(255 + (100 - progress * 2) * 2.55);
//
//        }
//
//        return Color.rgb(red, green, 0);
//    }
