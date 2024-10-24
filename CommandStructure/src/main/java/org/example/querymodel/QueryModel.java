package org.example.querymodel;

import org.example.domainmodel.Position;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class QueryModel {
    public static QueryModel instance = new QueryModel();
    public static QueryModel getInstance() {
        return instance;
    }

    private QueryModel() {}

    private static final HashMap<String, VehicleDTO> vehicleByName = new HashMap<String, VehicleDTO>();
    private static final HashMap<Position, List<VehicleDTO>> vehicleByPosition = new HashMap<Position, List<VehicleDTO>>();

    public Collection<VehicleDTO> getVehicles() {
        return vehicleByName.values();
    }

    public VehicleDTO getVehicleByName(String name) {
        return vehicleByName.get(name);
    }

    public Collection<VehicleDTO> getVehicleByPosition(Position position) {
        return vehicleByPosition.get(position);
    }

    public void addVehicle(VehicleDTO vehicle) {
        vehicleByName.put(vehicle.name(), vehicle);
        if (!vehicleByPosition.containsKey(vehicle.position())) {
            vehicleByPosition.put(vehicle.position(), new ArrayList<VehicleDTO>());
        }
        vehicleByPosition.get(vehicle.position()).add(vehicle);
    }

    public void moveVehicle(String name, Position vector) {
        VehicleDTO oldVehicle = vehicleByName.get(name);
        vehicleByPosition.get(oldVehicle.position()).remove(oldVehicle);
        VehicleDTO newVehicle = new VehicleDTO(
                new Position(
                  oldVehicle.position().x() + vector.x(),
                  oldVehicle.position().y() + vector.y()
                ),
                name,
                oldVehicle.numberOfMoves() + 1
        );
        vehicleByName.put(name, newVehicle);
        if (!vehicleByPosition.containsKey(newVehicle.position())) {
            vehicleByPosition.put(newVehicle.position(), new ArrayList<VehicleDTO>());
        }
        vehicleByPosition.get(newVehicle.position()).add(newVehicle);
    }

    public void removeVehicle(String name) {
        VehicleDTO oldVehicle = vehicleByName.get(name);
        vehicleByPosition.get(oldVehicle.position()).remove(oldVehicle);
        vehicleByName.remove(name);
    }
}
