package org.example.commands;

import org.example.domainmodel.Position;
import org.example.events.IEvent;

public class CreateVehicleCommand implements ICommand {

    String name;
    Position startPosition;

    public CreateVehicleCommand(String name, Position startPosition) {
        this.name = name;
        this.startPosition = startPosition;
    }

    public String getName() {
        return name;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    @Override
    public IEvent resolve() {
        return null;
    }

    @Override
    public Exception reject(String reason) throws Exception {
        throw new Exception(reason);
    }
}
