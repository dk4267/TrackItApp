package com.example.trackit;

import android.util.Log;

public class TrackCalculations {
    //Class takes care of calculations for the TrackerActivity class
    private double weight;
    private double lapsPerMile;
    private char units;

    private boolean isPaused = false;
    private long timeStopped = 0;
    private long startLapTime = 0;
    private double lapTime = 0;
    private double totalTime = 0;
    private double totalDistance = 0;
    private double averageSpeed = 0;
    private double lapAverageSpeed = 0;
    private double totalCalories = 0;
    private int numLaps = 0;
    private double lapSecsPerDistance = 0;
    private double avgSecsPerDistance = 0;


    public TrackCalculations() {}

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setLapsPerMile(double numLaps) {
        this.lapsPerMile = numLaps;
    }

    public char getUnits() {
        return units;
    }

    public void setUnits(char units) {
        this.units = units;
    }

    //Marks the timer as paused for future reference, and saves the time at which the timer was paused.
    public void pause(long timePaused) {
        this.isPaused = true;
        this.timeStopped = timePaused;
    }

    public long getTimePaused() {
        return this.timeStopped;
    }

    //Resets the pause variables after a pause has been taken care of
    public void unpause() {
        this.isPaused = false;
        this.timeStopped = 0;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setStartLapTime(long startLapTime) {
        this.startLapTime = startLapTime;
    }

    public void incrementLaps() {
        numLaps++;
    }

    //Updates the total time with the time for the previous lap, and resets the previous lap time
    public void updateTotalTime() {
        this.totalTime += this.lapTime;
        this.lapTime = 0;
    }

    public double getTotalTime() {
        return totalTime;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    public double getAverageSpeed() {
        return averageSpeed;
    }

    public double getTotalCalories() {
        return totalCalories;
    }

    public void incrementTotalDistance() {
        this.totalDistance += (1/lapsPerMile);
    }

    //Calculates average speed for the previous lap only
    public void findAverageLapSpeed(long endTime) {
        this.lapAverageSpeed = (1/lapsPerMile) / (this.lapTime / 3600);
    }

    //Calculates average speed for the entire session
    public void updateAverageSpeed() {
        this.averageSpeed *= (((double)this.numLaps - 1) / this.numLaps);
        this.averageSpeed += ((1 / (double)this.numLaps) * this.lapAverageSpeed);
    }

    //Calculates the MET value for the previous lap
    //Ranges are based on MET values from Compendium of Physical Activities for running and walking
    private double lapMETS() {
        double usSpeed;
        if (this.units == 'm') {
            usSpeed = this.lapAverageSpeed * 0.621371;
        }
        else {
            usSpeed = this.lapAverageSpeed;
        }
        if (usSpeed < 2) {
            return 2.0;
        }
        else if (usSpeed < 2.3){
            return 2.8;
        }
        else if (usSpeed < 2.6) {
            return 3.0;
        }
        else if (usSpeed < 3.2) {
            return 3.5;
        }
        else if (usSpeed < 3.7) {
            return 4.3;
        }
        else if (usSpeed < 4.2) {
            return 5.0;
        }
        else if (usSpeed < 4.7) {
            return 7.0;
        }
        else if (usSpeed < 5.1) {
            return 8.3;
        }
        else if (usSpeed < 5.5) {
            return 9.0;
        }
        else if (usSpeed < 6.3) {
            return 9.8;
        }
        else if (usSpeed < 6.8) {
            return 10.5;
        }
        else if (usSpeed < 7.3) {
            return 11.0;
        }
        else if (usSpeed < 7.7) {
            return 11.5;
        }
        else if (usSpeed < 8.3) {
            return 11.8;
        }
        else if (usSpeed < 8.8) {
            return 12.3;
        }
        else if (usSpeed < 9.5) {
            return 12.8;
        }
        else if (usSpeed < 10.5) {
            return 14.5;
        }
        else if (usSpeed < 11.5) {
            return 16.0;
        }
        else if (usSpeed < 12.5) {
            return 19.0;
        }
        else if (usSpeed < 13.5) {
            return 19.8;
        }
        else {
            return 23.0;
        }
    }

    //calculates calories burned in the previous lap
    //Uses the formula weight(kg) * MET * hours = total calories
    public void lapCalories() {
        double weightkg;
        double mets = lapMETS();
        double hours = ((this.lapTime) / 3600);

        if (this.units == 'i') {
            weightkg = this.weight / 2.2;
        }
        else {
            weightkg = this.weight;
        }
        this.totalCalories += mets * weightkg * hours;

    }
    public int getNumLaps() {
        return this.numLaps;
    }

    //Puts the total time in seconds into a time string format
    public String timeString() {
        int hours = (int) this.totalTime / 3600;
        int minutes = (int) ((this.totalTime % 3600) / 60);
        int secs =  (int) this.totalTime % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    //Resets all of the data to its starting values
    public void clearData() {
        this.isPaused = false;
        this.startLapTime = 0;
        this.totalTime = 0;
        this.totalDistance = 0;
        this.averageSpeed = 0;
        this.lapAverageSpeed = 0;
        this.totalCalories = 0;
        this.numLaps = 0;
        this.lapSecsPerDistance = 0;
        this.avgSecsPerDistance = 0;
    }

    //Calculates the time for the previous lap
    public void lapEnd(long endTime) {
        this.lapTime = (double)((endTime - startLapTime) / 1000);
    }

    //Calculates the average pace for the workout and puts it in time string format
    //If statement at beginning handles case when the user has just reset the app, and this.numLaps = 0
    public String getAvgPace() {
        boolean reset = false;
        if (this.numLaps == 0) {
            this.numLaps++;
            reset = true;
        }
        this.avgSecsPerDistance *= (((double)this.numLaps - 1) / this.numLaps);
        this.avgSecsPerDistance += ((1 / (double)this.numLaps) * this.lapSecsPerDistance);
        int minutes = (int) avgSecsPerDistance / 60;
        int secs = (int) avgSecsPerDistance % 60;
        if (reset) {
            this.numLaps--;
        }
        return String.format("%02d:%02d", minutes, secs);

    }

    //Calculates pace for the previous lap in secs per unit distance
    public void setLapPace(long lapEndTime) {
        this.lapSecsPerDistance = ((lapEndTime - this.startLapTime) / 1000.0) / (1/(double)this.lapsPerMile);
    }

    //Returns pace for the previous lap in a time string format
    public String getLapPace() {
        int minutes = (int)this.lapSecsPerDistance / 60;
        int secs = (int)this.lapSecsPerDistance % 60;

        return String.format("%02d:%02d", minutes, secs);
    }
}
