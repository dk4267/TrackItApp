package com.example.trackit;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

public class TrackerActivity extends AppCompatActivity {

    private Button startButton;
    private Button pauseButton;
    private Button stopButton;
    private Button newLapButton;
    private Button resetButton;
    private Button backButton;

    private TextView lapNum;
    private TextView totalTime;
    private TextView totalDistance;
    private TextView totalDistanceUnits;
    private TextView averagePace;
    private TextView averagePaceUnits;
    private TextView prevLapPace;
    private TextView prevLapPaceUnits;
    private TextView calsBurned;

    private final int REQUEST_CODE = 2;
    private Chronometer timer;

    TrackCalculations calc = new TrackCalculations();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracker);

        //Set up all the buttons and textviews
        startButton = findViewById(R.id.start_button);
        pauseButton = findViewById(R.id.pause_button);
        stopButton = findViewById(R.id.stop_button);
        newLapButton = findViewById(R.id.new_lap_button);
        resetButton = findViewById(R.id.reset_button);
        backButton = findViewById(R.id.back_button);

        lapNum = findViewById(R.id.lap_number);
        totalTime = findViewById(R.id.total_time);
        totalDistance = findViewById(R.id.total_distance);
        totalDistanceUnits = findViewById(R.id.total_distance_units);
        averagePace = findViewById(R.id.average_speed);
        averagePaceUnits = findViewById(R.id.avg_pace_units);
        prevLapPace = findViewById(R.id.prev_lap_speed);
        prevLapPaceUnits = findViewById(R.id.prev_lap_units);
        calsBurned = findViewById(R.id.cals_burned);

        //Set up the timer that displays the current lap time
        timer = findViewById(R.id.lap_time);

        double weight;
        double laps;
        char units;

        //Start with all the buttons except the start and back buttons disabled
        pauseButton.setEnabled(false);
        stopButton.setEnabled(false);
        newLapButton.setEnabled(false);
        resetButton.setEnabled(false);

        //Get weight, laps per mile, and units data from last activity
        Bundle extra = getIntent().getExtras();
        weight = extra.getFloat("weight");
        laps = extra.getFloat("laps");
        units = extra.getChar("units");

        //Set the data in the TrackCalculations class to be used for calculations
        calc.setWeight(weight);
        calc.setLapsPerMile(laps);
        calc.setUnits(units);

        //Set units on screen to metric, if necessary
        if (units == 'm') {
            totalDistanceUnits.setText("km");
            averagePaceUnits.setText("min/km");
            prevLapPaceUnits.setText("min/km");
        }

        startButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //If not restarting after a pause, start timer normally
                        if (!calc.isPaused()){
                            timer.setBase(SystemClock.elapsedRealtime());
                            timer.start();
                        }
                        //If restarting after a pause, set timer to reflect it, and reset the
                        //pause variables in TrackCalculations
                        else {
                            timer.setBase(SystemClock.elapsedRealtime() + calc.getTimePaused());
                            timer.start();
                            calc.unpause();
                        }

                        //Save start time, enable and disable proper buttons
                        long startTime = System.currentTimeMillis();
                        calc.setStartLapTime(startTime);
                        pauseButton.setEnabled(true);
                        stopButton.setEnabled(true);
                        newLapButton.setEnabled(true);
                        resetButton.setEnabled(true);
                        startButton.setEnabled(false);
                    }
                }
        );
        pauseButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Save the pause time, stop the timer
                        long timeStopped = timer.getBase() - SystemClock.elapsedRealtime();
                        timer.stop();
                        calc.pause(timeStopped);
                        long pauseTime = System.currentTimeMillis();
                        calc.lapEnd(pauseTime);

                        startButton.setEnabled(true);
                        pauseButton.setEnabled(false);
                        stopButton.setEnabled(true);
                        newLapButton.setEnabled(false);
                        resetButton.setEnabled(true);
                    }
                }
        );
        stopButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //stop timer, save stop time, and update the total time with the
                        //current lap time
                        timer.stop();
                        long stopTime = System.currentTimeMillis();
                        calc.lapEnd(stopTime);
                        calc.updateTotalTime();

                        //Start the final activity and pass the data to it
                        Intent intent = new Intent(TrackerActivity.this, ResultsActivity.class);
                        intent.putExtra("totalTime", calc.getTotalTime());
                        intent.putExtra("totalDistance", calc.getTotalDistance());
                        intent.putExtra("averageSpeed", calc.getAverageSpeed());
                        intent.putExtra("totalCalories", calc.getTotalCalories());
                        intent.putExtra("units", calc.getUnits());
                        intent.putExtra("averagePace", calc.getAvgPace());
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                }
        );
        newLapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Update time, laps, total distance, lap speed and pace, and calories
                long lapEndTime = System.currentTimeMillis();
                calc.lapEnd(lapEndTime);
                calc.incrementLaps();
                calc.incrementTotalDistance();
                calc.findAverageLapSpeed(lapEndTime);
                calc.setLapPace(lapEndTime);
                calc.updateAverageSpeed();
                calc.lapCalories();
                calc.updateTotalTime();

                //Stom the timer, reset to zero, restart it from zero
                timer.stop();
                timer.setBase(SystemClock.elapsedRealtime());
                timer.start();

                //Display updated info on the screen
                lapNum.setText(Integer.toString(calc.getNumLaps() + 1));
                totalTime.setText(calc.timeString());
                totalDistance.setText(String.format("%.2f", calc.getTotalDistance()));
                averagePace.setText(calc.getAvgPace());
                prevLapPace.setText(calc.getLapPace());
                calsBurned.setText(String.format("%.1f",calc.getTotalCalories()));
                calc.setStartLapTime(lapEndTime);

                startButton.setEnabled(false);
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
                resetButton.setEnabled(true);
                backButton.setEnabled(true);
            }
        });
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stop timer, reset all data, display reset data
                timer.stop();
                calc.clearData();
                lapNum.setText(Integer.toString(calc.getNumLaps() + 1));
                totalTime.setText(calc.timeString());
                totalDistance.setText(String.format("%.2f", calc.getTotalDistance()));
                averagePace.setText(calc.getAvgPace());
                prevLapPace.setText(calc.getLapPace());
                calsBurned.setText(String.format("%.1f", calc.getTotalCalories()));

                startButton.setEnabled(true);
                pauseButton.setEnabled(false);
                stopButton.setEnabled(false);
                newLapButton.setEnabled(false);
                resetButton.setEnabled(false);
                backButton.setEnabled(true);
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Stop timer, reset all data, go back to the first activity
                timer.stop();
                calc.clearData();
                Intent intent = new Intent(TrackerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
