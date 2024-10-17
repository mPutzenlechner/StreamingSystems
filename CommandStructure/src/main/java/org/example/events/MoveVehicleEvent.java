package org.example.events;

import org.example.domainmodel.Position;

public class MoveVehicleEvent implements IEvent {
    String name;
    Position vector;

    public MoveVehicleEvent(String name, Position vector) {
        this.name = name;
        this.vector = vector;
    }
}
