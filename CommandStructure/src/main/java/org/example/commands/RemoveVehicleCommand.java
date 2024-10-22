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
}
