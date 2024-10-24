package org.example.querymodel;
import org.example.domainmodel.Position;

public record VehicleDTO(Position position, String name, int numberOfMoves) implements IVehicleDTO {
}
