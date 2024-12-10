package org.example.querymodel;
import org.example.querymodel.Position;

public record VehicleDTO(Position position, String name, int numberOfMoves) implements IVehicleDTO {
}
