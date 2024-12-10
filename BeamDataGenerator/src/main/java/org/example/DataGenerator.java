package org.example;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;


public class DataGenerator {

    private int numSensors = 5;
    private int maxDatasets = 3;
    private int intervalMsMin = 400;
    private int intervalMsMax = 1000;
    private float vMin = -20.0f;
    private float vMax = 100.0f;
    private Random random = new Random();
    private Thread thread;

    // Set internal data generation parameters
    public DataGenerator() {
        // Start sending data
        thread = new Thread(() -> {
            while (true) {
                try {
                    String data = generate();
                    sendData(data);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    // Start generating traffic data
    private String generate() throws InterruptedException {
        // Wait a random amount of time
        int intervalMsRange = intervalMsMax - intervalMsMin;  // calculate the interval range
        int intervalMs = random.nextInt(intervalMsRange) + intervalMsMin;  // decide how long to wait
        Thread.sleep(intervalMs);
        // Generate parameters
        float vRange = vMax - vMin;  // calculate range of speed values
        int sensor = random.nextInt(numSensors) + 1;  // Decide which sensor has sent the dataset
        int numValues = random.nextInt(maxDatasets + 1);  // decide how many datasets to generate
        // Generate data
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        String date = df.format(new Date());
        StringBuilder data = new StringBuilder();
        data.append(date).append(" ").append(sensor).append(" ");
        for (int i = 0; i < numValues; i++) {
            data.append(random.nextFloat() * vRange + vMin).append(" ");
        }

        return data.toString();
    }

    // Send data to Apache Beam
    private void sendData(String data) {
        // Stub until apache beam has been set up
        System.out.println(data);
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }
}
