package org.example.model;
import org.example.model.Position;

public record VehicleDTO(Position position, String name, int numberOfMoves) implements IVehicleDTO {
}
