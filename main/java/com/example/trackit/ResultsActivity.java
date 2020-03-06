package com.example.trackit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {

    private TextView totalTime;
    private TextView totalDistance;
    private TextView totalDistanceUnits;
    private TextView averageSpeed;
    private TextView averageSpeedUnits;
    private TextView calsBurned;
    private TextView averagePace;
    private TextView averagePaceUnits;
    private Button startOver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results2);

        //Initialize all the textviews
        totalTime = findViewById(R.id.finalTotalTime);
        totalDistance = findViewById(R.id.finalTotalDistance);
        averageSpeed = findViewById(R.id.finalAvgSpeed);
        calsBurned = findViewById(R.id.finalCalsBurned);
        totalDistanceUnits = findViewById(R.id.finalDistanceUnits);
        averageSpeedUnits = findViewById(R.id.finalSpeedUnits);
        startOver = findViewById(R.id.startOver);
        averagePace = findViewById(R.id.minMileValue);
        averagePaceUnits = findViewById(R.id.minmileunits);

        double time;
        double distance;
        double speed;
        double cals;
        char units;
        String pace;

        //Save all the data from the previous activity into variables
        Bundle extra = getIntent().getExtras();
        time = extra.getDouble("totalTime");
        distance = extra.getDouble("totalDistance");
        speed = extra.getDouble("averageSpeed");
        cals = extra.getDouble("totalCalories");
        units = extra.getChar("units");
        pace = extra.getString("averagePace");

        //Set units on screen to metric if necessary
        if (units == 'm') {
            totalDistanceUnits.setText("km");
            averageSpeedUnits.setText("km/h");
            averagePaceUnits.setText("min/km");
        }

        //Display all results on the screen
        int hours = (int) time / 3600;
        int minutes = (int) ((time % 3600) / 60);
        int secs =  (int) time % 60;
        String timeResult = String.format("%02d:%02d:%02d", hours, minutes, secs);
        totalTime.setText(timeResult);
        totalDistance.setText(String.format("%.2f",distance));
        averageSpeed.setText(String.format("%.2f",speed));
        calsBurned.setText(String.format("%.1f",cals));
        averagePace.setText(pace);

        startOver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Go back to first activity without sending any data
                Intent intent = new Intent(ResultsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
