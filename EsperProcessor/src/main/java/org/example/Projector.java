package org.example;

import com.espertech.esper.runtime.client.EPRuntime;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;


// Should receive events from the jms and update the query model accordingly
public class Projector {

    private final Logger logger;
    private Consumer<String, String> consumer;
    private EPRuntime runtime;

    Projector(EPRuntime runtime) {
        this.logger = LoggerFactory.getLogger(Projector.class);
        this.logger.debug("Projector initialized");
        this.runtime = runtime;
        KafkaConnector eventStoreService = KafkaConnector.getInstance();
        String topic = "beamdata";
        try {
            this.consumer = eventStoreService.getConsumer("projector");
            consumer.assign(Collections.singleton(new TopicPartition(topic, 0)));
            new Thread(this::startMessageHandling).start();
        } catch (Exception e) {
            this.logger.error("Projector initialization failed with Error: {}", e.getMessage());
        }
    }


    private void startMessageHandling() {
        // Start reading messages from the beginning
        while (true) {
            consumer
                    .poll(Duration.ofMillis(100))
                    .iterator()
                    .forEachRemaining(
                            record -> {
                                try {
                                    this.logger.debug("received kafka record on topic: {}", record.topic());
                                    handleMessage(record.value());
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                    );
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                this.logger.error(e.getMessage());
            }
        }
    }

    private void handleMessage(String message) throws JsonProcessingException {
        try {
            String[] parts = message.split(" ");
            if (parts.length < 3) return;  // Ignore if no speed data provided

            int sensorId = Integer.parseInt(parts[1]);
            String[] speedStrings = parts[2].split(",");

            for (String speedStr : speedStrings) {
                double speed = Double.parseDouble(speedStr) * 3.6;  // Convert m/s to km/h
                if (speed < 0) {  // Filter measuring errors
                    continue;
                }
                runtime.getEventService().sendEventBean(new SensorEvent(parts[0], sensorId, speed), "SensorEvent");
            }
        } catch (Exception e) {
            // Ignore faulty datasets
        }
    }
}
