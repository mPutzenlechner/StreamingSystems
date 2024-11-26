package org.example.events;
import org.example.domainmodel.Position;


public record CreateVehicleEvent(String name, Position startPosition) implements IEvent {
}
