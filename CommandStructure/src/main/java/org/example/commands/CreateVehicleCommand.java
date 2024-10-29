package org.example.commands;

import org.example.domainmodel.Position;

public record CreateVehicleCommand(String name, Position startPosition) implements ICommand {

}
