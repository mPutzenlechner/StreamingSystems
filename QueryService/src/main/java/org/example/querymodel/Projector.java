package org.example.querymodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.example.events.CreateVehicleEvent;
import org.example.events.IEvent;
import org.example.events.MoveVehicleEvent;
import org.example.events.RemoveVehicleEvent;
import org.example.services.EventStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.Collections;


// Should receive events from the jms and update the query model accordingly
public class Projector {
    private static final Projector instance = new Projector();

    public static Projector getInstance() {
        return instance;
    }

    private final Logger logger;
    private Consumer<String, String> consumer;
    private final QueryModel queryModel;


    private Projector() {
        this.logger = LoggerFactory.getLogger(Projector.class);
        this.logger.debug("Projector initialized");
        this.queryModel = QueryModel.getInstance();
        EventStoreService eventStoreService = EventStoreService.getInstance();
        String topic = "events";
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
        consumer.seekToBeginning(consumer.assignment());
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
        // Deserialize
        IEvent event = new ObjectMapper().readerFor(IEvent.class).readValue(message);
        if (event instanceof CreateVehicleEvent createVehicleEvent) {
            this.handleEvent(createVehicleEvent);
        } else if (event instanceof MoveVehicleEvent moveVehicleEvent) {
            this.handleEvent(moveVehicleEvent);
        } else if (event instanceof RemoveVehicleEvent removeVehicleEvent) {
            this.handleEvent(removeVehicleEvent);
        }
    }

    private void handleEvent(CreateVehicleEvent createVehicleEvent) {
        VehicleDTO vehicleDTO = new VehicleDTO(
                createVehicleEvent.startPosition(),
                createVehicleEvent.name(),
                0
        );
        this.queryModel.addVehicle(vehicleDTO);
    }

    private void handleEvent(MoveVehicleEvent moveVehicleEvent) {
        this.queryModel.moveVehicle(moveVehicleEvent.name(), moveVehicleEvent.vector());
    }

    private void handleEvent(RemoveVehicleEvent removeVehicleEvent) {
        this.queryModel.removeVehicle(removeVehicleEvent.name());
    }
}
