package org.example.commands;

import org.example.domainmodel.Position;

public record MoveVehicleCommand(String name, Position vector) implements ICommand {
}
