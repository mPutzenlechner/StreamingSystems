package org.example;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.transforms.*;
import org.apache.beam.sdk.values.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.beam.sdk.transforms.windowing.*;
import org.joda.time.Duration;

public class Main {

    public static void main(String[] args) {
        // Define window size in seconds
        int windowSizeSec = 30;

        Pipeline pipeline = Pipeline.create();

        // Read events from kafka topic
        PCollection<String> kafkaMessages = pipeline.apply(KafkaIO.<String, String>read()
                        .withBootstrapServers("localhost:9092") // Kafka-Broker-URL
                        .withTopic("beamdata") // Topic-Name
                        .withKeyDeserializer(StringDeserializer.class)
                        .withValueDeserializer(StringDeserializer.class)
                        .withoutMetadata())
                .apply(Values.create());

        // Parse kafka messages and filter invalid data
        PCollection<KV<Integer, Double>> sensorSpeeds = kafkaMessages
                .apply(ParDo.of(new DoFn<String, KV<Integer, Double>>() {
                    @ProcessElement
                    public void processElement(@Element String message, OutputReceiver<KV<Integer, Double>> out) {
                        try {
                            String[] parts = message.split(" ");
                            if (parts.length < 3) return;  // Ignore if no speed data provided

                            int sensorId = Integer.parseInt(parts[1]);
                            String[] speedStrings = parts[2].split(",");

                            for (String speedStr : speedStrings) {
                                double speed = Double.parseDouble(speedStr) * 3.6;  // Convert m/s to km/h
                                out.output(KV.of(sensorId, speed));
                            }
                        } catch (Exception e) {
                            // Ignore faulty datasets
                        }
                    }
                }));

        // Calculate average speed per sensor
        PCollection<String> averageSpeeds = sensorSpeeds
                .apply(Window.<KV<Integer, Double>>into(FixedWindows.of(Duration.standardSeconds(windowSizeSec)))) // Apply windowing for defined duration
                .apply(Combine.perKey(Mean.of()))
                .apply(ParDo.of(new DoFn<KV<Integer, Double>, String>() {
                    @ProcessElement
                    public void processElement(@Element KV<Integer, Double> element, OutputReceiver<String> out) {
                        int sensorId = element.getKey();
                        double avgSpeed = element.getValue();
                        out.output("Sensor " + sensorId + ": Average speed = " + avgSpeed + " km/h");
                    }
                }));

        // Write results to console
        averageSpeeds.apply(ParDo.of(new DoFn<String, Void>() {
            @ProcessElement
            public void processElement(@Element String output) {
                System.out.println(output);
            }
        }));
        // Run the pipeline
        pipeline.run().waitUntilFinish();
    }
}