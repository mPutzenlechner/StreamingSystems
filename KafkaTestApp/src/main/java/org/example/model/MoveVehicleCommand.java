package org.example.model;

import org.example.model.Position;

public record MoveVehicleCommand(String name, Position vector) implements ICommand {
}
