package org.example.domainmodel;
import java.util.HashMap;
import java.util.Map;


public class VehicleRegister {
    private static final VehicleRegister instance = new VehicleRegister();

    private final Map<String, Vehicle> vehicleRegister = new HashMap<>();

    public static VehicleRegister getInstance() {
        return instance;
    }

    public boolean vehicleExists(String name) {
        return vehicleRegister.containsKey(name);
    }

    public void moveVehicle(String name, Position vector) {
        Vehicle vehicle = vehicleRegister.get(name);
        vehicle.currentPosition = new Position(
                vehicle.currentPosition.x() + vector.x(),
                vehicle.currentPosition.y() + vector.y()
        );
        vehicleRegister.replace(name, vehicle);
    }

    public Position getPosition(String name) {
        return vehicleRegister.get(name).currentPosition;
    }

    public void createVehicle(String name, Vehicle vehicle) {
        vehicleRegister.put(name, vehicle);
    }

    public void deleteVehicle(String name) {
        vehicleRegister.remove(name);
    }
}
