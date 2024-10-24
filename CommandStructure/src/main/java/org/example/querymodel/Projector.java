package org.example.querymodel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.*;
import org.example.events.CreateVehicleEvent;
import org.example.events.IEvent;
import org.example.events.MoveVehicleEvent;
import org.example.events.RemoveVehicleEvent;
import org.example.services.EventStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Should receive events from the jms and update the query model accordingly
public class Projector {
    private static final Projector instance = new Projector();

    public static Projector getInstance() {
        return instance;
    }

    private final Logger logger;
    private MessageConsumer consumer;
    private QueryModel queryModel;

    private Projector() {
        this.logger = LoggerFactory.getLogger(Projector.class);
        this.queryModel = QueryModel.getInstance();
        EventStoreService eventStoreService = EventStoreService.getInstance();
        try {
            this.consumer = eventStoreService.getConsumer();
            this.startMessageHandling();
        } catch (Exception e) {
            this.logger.error("Projector initialization failed with Error: {}", e.getMessage());
        }
    }

    private void startMessageHandling() throws JMSException {
        this.consumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    handleMessages(message);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void handleMessages(Message message) throws JMSException, JsonProcessingException {
        if (!(message instanceof TextMessage textMessage)) {
            return;
        }
        // Deserialize
        IEvent event = new ObjectMapper().readerFor(IEvent.class).readValue(textMessage.getText());
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
