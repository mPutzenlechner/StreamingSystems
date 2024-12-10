package org.example.events;
import org.example.querymodel.Position;


public record CreateVehicleEvent(String name, Position startPosition) implements IEvent {
}
