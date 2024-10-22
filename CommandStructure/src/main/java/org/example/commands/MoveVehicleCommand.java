package org.example.commands;

import org.example.domainmodel.Position;
import org.example.events.IEvent;
import org.example.events.MoveVehicleEvent;

public class MoveVehicleCommand implements ICommand {
    String name;
    Position vector;

    public MoveVehicleCommand(String name, Position vector) {
        this.name = name;
        this.vector = vector;
    }

    public Position getVector() {
        return vector;
    }

    public String getName() {
        return name;
    }
}
