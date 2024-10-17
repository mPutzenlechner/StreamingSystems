package org.example.querymodel;

import org.example.domainmodel.Position;

import java.util.Enumeration;

public interface Query {
    public VehicleDTO getVehicleByName(String name);
    public Enumeration<VehicleDTO> getVehicles();
    public Enumeration<VehicleDTO> getVehiclesAtPosition(Position position);
}
