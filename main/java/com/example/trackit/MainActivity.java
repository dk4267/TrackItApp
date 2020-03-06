package com.example.trackit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {

    private ToggleButton unitsToggle;
    private EditText enterWeight;
    private EditText enterLaps;
    private Button submit;
    private final int REQUEST_CODE = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unitsToggle = findViewById(R.id.us_metric_toggle_button);
        enterWeight = findViewById(R.id.weight_input);
        enterLaps = findViewById(R.id.laps_input);
        submit = findViewById(R.id.submit_button);


        submit.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View v) {
                    try {
                        //Get and save weight, unit, and laps per mile data
                        String weightString = enterWeight.getText().toString();
                        float weight = Float.valueOf(weightString.trim());

                        String lapsString = enterLaps.getText().toString();
                        float laps = Float.valueOf(lapsString.trim());

                        char units;
                        if (unitsToggle.isChecked()) {
                            units = 'm';
                        }
                        else {
                            units = 'i';
                        }

                        //Pass weight, unit, and laps per mile data to the next activity, and open next activity
                        Intent intent = new Intent(MainActivity.this, TrackerActivity.class);
                        intent.putExtra("weight", weight);
                        intent.putExtra("laps", laps);
                        intent.putExtra("units", units);
                        startActivityForResult(intent, REQUEST_CODE);
                    }
                    //This shouldn't happen, but just in case
                    catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Invalid Input", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        );
    }
}
