package org.example.events;

import org.example.domainmodel.Position;

public record MoveVehicleEvent(String name, Position vector) implements IEvent {

}
