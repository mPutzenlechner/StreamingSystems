package org.example.model;

import org.example.model.Position;

public record CreateVehicleCommand(String name, Position startPosition) implements ICommand {

}
