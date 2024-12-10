package org.example.events;

import org.example.querymodel.Position;

public record MoveVehicleEvent(String name, Position vector) implements IEvent {

}
