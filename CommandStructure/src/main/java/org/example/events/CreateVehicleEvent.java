package org.example.events;
import org.example.domainmodel.Position;


public class CreateVehicleEvent implements IEvent {
    String name;
    Position startPosition;

    public CreateVehicleEvent(String name, Position startPosition) {
        this.name = name;
        this.startPosition = startPosition;
    }
}
