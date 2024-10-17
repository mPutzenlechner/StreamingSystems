package org.example.commandhandler;

import org.example.domainmodel.Position;

public interface IVehicleCommands {
    void createVehicle(String name, Position startPosition) throws Exception;
    void moveVehicle(String name, Position moveVector) throws Exception;
    void removeVehicle(String name) throws Exception;
}
