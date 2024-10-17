package org.example.events;

public class RemoveVehicleEvent implements IEvent {
    String name;

    public RemoveVehicleEvent(String name) {
        this.name = name;
    }
}
