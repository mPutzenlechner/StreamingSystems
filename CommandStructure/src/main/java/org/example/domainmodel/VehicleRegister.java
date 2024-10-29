package org.example.domainmodel;
import org.example.querymodel.VehicleDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class VehicleRegister {
    private static final VehicleRegister instance = new VehicleRegister();

    private final Map<String, Vehicle> vehicleRegister = new HashMap<>();
    private final HashMap<Position, String> positionMap = new HashMap<Position, String>();

    public static VehicleRegister getInstance() {
        return instance;
    }

    public Vehicle getVehicle(String name) {
        return vehicleRegister.get(name);
    }

    public boolean vehicleExists(String name) {
        return vehicleRegister.containsKey(name);
    }

    public void moveVehicle(String name, Position vector) {
        Vehicle vehicle = vehicleRegister.get(name);
        positionMap.remove(vehicle.currentPosition);
        vehicle.move(vector);
        vehicleRegister.replace(name, vehicle);
        positionMap.put(vehicle.currentPosition, name);
    }

    public Position getPosition(String name) {
        return vehicleRegister.get(name).currentPosition;
    }

    public void createVehicle(String name, Vehicle vehicle) {
        vehicleRegister.put(name, vehicle);
        positionMap.put(vehicle.currentPosition, name);
    }

    public void deleteVehicle(String name) {
        Position oldPosition = vehicleRegister.get(name).currentPosition;
        vehicleRegister.remove(name);
        positionMap.remove(oldPosition);
    }

    public String getVehicleOnPosition(Position position) {
        if (positionMap.containsKey(position)) {
            return positionMap.get(position);
        }
        return null;
    }
}
