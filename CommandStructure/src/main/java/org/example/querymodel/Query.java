package org.example.querymodel;

import org.example.domainmodel.Position;

import java.util.Collection;
import java.util.Enumeration;

public interface Query {
    public VehicleDTO getVehicleByName(String name);
    public Collection<VehicleDTO> getVehicles();
    public Collection<VehicleDTO> getVehiclesAtPosition(Position position);
}
