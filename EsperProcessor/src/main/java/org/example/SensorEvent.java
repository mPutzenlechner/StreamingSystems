package org.example;

public class SensorEvent {
    private String timestamp;
    private int sensorId;
    private double speed;

    public SensorEvent(String timestamp, int sensorId, double speed) {
        this.timestamp = timestamp;
        this.sensorId = sensorId;
        this.speed = speed;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getSensorId() {
        return sensorId;
    }

    public double getSpeed() {
        return speed;
    }
}