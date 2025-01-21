package org.example;

public class SpeedDecreaseEvent {

    // Definition der SpeedDecreaseEvent-Klasse
    private int sensorId;
    private double prevAvgSpeed;
    private double currAvgSpeed;

    public SpeedDecreaseEvent(int sensorId, double prevAvgSpeed, double currAvgSpeed) {
        this.sensorId = sensorId;
        this.prevAvgSpeed = prevAvgSpeed;
        this.currAvgSpeed = currAvgSpeed;
    }

    public int getSensorId() {
        return sensorId;
    }

    public double getPrevAvgSpeed() {
        return prevAvgSpeed;
    }

    public double getCurrAvgSpeed() {
        return currAvgSpeed;
    }
}
