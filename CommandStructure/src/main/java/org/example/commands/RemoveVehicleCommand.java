package org.example.commands;

import org.example.events.IEvent;
import org.example.events.RemoveVehicleEvent;

public class RemoveVehicleCommand implements ICommand {
    String name;
    public RemoveVehicleCommand(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public IEvent resolve() {
        return new RemoveVehicleEvent(this.name);
    }

    @Override
    public Exception reject(String reason) throws Exception {
        throw new Exception(reason);
    }
}
