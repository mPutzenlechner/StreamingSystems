package org.example.domainmodel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.kafka.clients.consumer.Consumer;
import org.example.events.CreateVehicleEvent;
import org.example.events.IEvent;
import org.example.events.MoveVehicleEvent;
import org.example.events.RemoveVehicleEvent;
import org.example.querymodel.Projector;
import org.example.services.EventStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;


public class DomainModel {
    private static final DomainModel instance = new DomainModel();
    private final Logger logger;
    private Consumer<String, String> consumer;
    private final ObjectReader objectReader = new ObjectMapper().readerFor(IEvent.class);

    public static DomainModel getInstance() {
        return instance;
    }

    private DomainModel() {
        this.logger = LoggerFactory.getLogger(Projector.class);
        this.logger.debug("Projector initialized");
        EventStoreService eventStoreService = EventStoreService.getInstance();
        String topic = "events";
        try {
            this.consumer = eventStoreService.getConsumer("domainmodel");
            consumer.subscribe(List.of(topic));
        } catch (Exception e) {
            this.logger.error("Domain model initialization failed with Error: {}", e.getMessage());
        }
    }

    // Return all messages in the topic
    private List<IEvent> getRecords() {
        // Reset consumer to beginning
        this.consumer.seekToBeginning(consumer.assignment());
        List<IEvent> events = new ArrayList<>();
        consumer
                .poll(Duration.ofMillis(200))
                .iterator()
                .forEachRemaining(
                        record -> {
                            try {
                                events.add(this.objectReader.readValue(record.value()));
                            } catch (JsonProcessingException e) {
                                // Do nothing, but inform user
                                logger.error(e.getMessage());
                            }
                        }
                );
        return events;
    }

    public Vehicle getVehicle(String name) {
        AtomicReference<Vehicle> vehicle = new AtomicReference<>();
        // Collect events for requested vehicle
        List<IEvent> events = getRecords()
                .stream()
                .filter(event -> {
                    if (event instanceof CreateVehicleEvent) {
                        return ((CreateVehicleEvent) event).name().equals(name);
                    } else if (event instanceof MoveVehicleEvent) {
                        return ((MoveVehicleEvent) event).name().equals(name);
                    } else {
                        return ((RemoveVehicleEvent) event).name().equals(name);
                    }
                }).toList();
        // Build vehicle from events
        events.iterator()
                .forEachRemaining(
                event -> {
                    if (event instanceof CreateVehicleEvent createEvent) {
                        vehicle.set(new Vehicle(createEvent.name(), createEvent.startPosition()));
                    } else if (event instanceof MoveVehicleEvent moveEvent) {
                        Vehicle vehicleInst = vehicle.get();
                        vehicleInst.move(moveEvent.vector());
                        vehicle.set(vehicleInst);
                    } else  {
                        vehicle.set(null);  // Vehicle has been removed
                    }
                }
        );

        return vehicle.get();
    }

    public boolean vehicleExists(String name) {
        List<IEvent> events = getRecords();
        AtomicReference<Boolean> exists = new AtomicReference<>(false);
        events.stream()
                .filter(event -> event instanceof CreateVehicleEvent || event instanceof RemoveVehicleEvent)
                .filter(event -> {
                    if (event instanceof CreateVehicleEvent) {
                        return ((CreateVehicleEvent) event).name().equals(name);
                    } else {
                        return ((RemoveVehicleEvent) event).name().equals(name);
                    }
                })
                .iterator()
                .forEachRemaining(event -> {
                    if (event instanceof CreateVehicleEvent) {
                        exists.set(true);
                    } else {
                        exists.set(false);
                    }
                });
        logger.debug("vehicle exists: {}", exists.get());
        return exists.get();
    }

    public String getVehicleOnPosition(Position position) {
        Map<String, Vehicle> vehicleMap = new HashMap<>();
        List<IEvent> events = getRecords();
        // Recreate entire domainmodel state
        events.iterator().forEachRemaining(event -> {
            if (event instanceof CreateVehicleEvent createEvent) {
                vehicleMap.put(createEvent.name(), new Vehicle(createEvent.name(), createEvent.startPosition()));
            } else if (event instanceof MoveVehicleEvent moveEvent) {
                Vehicle vehicleInst = vehicleMap.get(moveEvent.name());
                vehicleInst.move(moveEvent.vector());
                vehicleMap.put(
                        moveEvent.name(),
                        vehicleInst
                );
            } else if (event instanceof RemoveVehicleEvent removeEvent) {
                vehicleMap.remove(removeEvent.name());
            }
        });
        for (Vehicle vehicle : vehicleMap.values()) {
            if (vehicle.currentPosition == position) {
                return vehicle.name;
            }
        }
        return null;
    }
}
