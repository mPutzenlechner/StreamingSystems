package org.example.commandhandler;
import org.example.commands.CreateVehicleCommand;
import org.example.commands.MoveVehicleCommand;
import org.example.commands.RemoveVehicleCommand;
import org.example.domainmodel.Position;
import org.example.domainmodel.Vehicle;
import org.example.domainmodel.DomainModel;
import org.example.events.CreateVehicleEvent;
import org.example.events.IEvent;
import org.example.events.MoveVehicleEvent;
import org.example.events.RemoveVehicleEvent;
import org.example.services.EventStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleCommandHandler {
    private static final VehicleCommandHandler instance = new VehicleCommandHandler();

    private final Logger logger = LoggerFactory.getLogger(VehicleCommandHandler.class);

    private final DomainModel vehicleRegister;
    private final EventStoreService eventStoreService;


    public VehicleCommandHandler() {
        vehicleRegister = DomainModel.getInstance();
        eventStoreService = EventStoreService.getInstance();
        logger.debug("Command handler initialized");
    }

    public static VehicleCommandHandler getInstance() {
        return instance;
    }

    public void issueCommand(CreateVehicleCommand command) throws Exception {
        // Check if vehicle already exists
        String name = command.name();
        if (vehicleRegister.vehicleExists(name)) {
            throw new Exception("vehicle with name " + name + "already exists");
        }
        // Check if there is a vehicle on start position, that needs to be removed.
        String vehicleOnPosition = this.vehicleRegister.getVehicleOnPosition(command.startPosition());
         if (vehicleOnPosition != null) {
            this.issueCommand(new RemoveVehicleCommand(vehicleOnPosition));
        }
        // Does not exist. Generate event.
        Vehicle vehicle = new Vehicle(command.name(), command.startPosition());
        IEvent event = new CreateVehicleEvent(command.name(), command.startPosition());
        this.eventStoreService.raiseEvent(event, vehicle.name);
    }

    public void issueCommand(MoveVehicleCommand command) throws Exception {
        // Check validity
        if (command.vector() == null || command.vector().x() == 0 && command.vector().y() == 0) {
            throw new Exception("invalid vector");
        }
        // Check if vehicle should be removed because it moved often enough, or was on this position before.
        Vehicle vehicle = this.vehicleRegister.getVehicle(command.name());
        Position newPosition = new Position(
                vehicle.currentPosition.x() + command.vector().x(),
                vehicle.currentPosition.y() + command.vector().y()
        );
        if (vehicle.getPositionHistory().contains(newPosition) || vehicle.getNumberOfMoves() >= 20) {
            // remove vehicle
            this.issueCommand(new RemoveVehicleCommand(command.name()));
            return;
        }
        // Vehicle should not be removed. Check if there is a vehicle on new position, that needs to be removed.
        String vehicleOnPosition = this.vehicleRegister.getVehicleOnPosition(newPosition);
        if (vehicleOnPosition != null) {
            this.issueCommand(new RemoveVehicleCommand(vehicleOnPosition));
        }
        // Valid, Done. Resolve.
        IEvent event = new MoveVehicleEvent(command.name(), command.vector());
        this.eventStoreService.raiseEvent(event, vehicle.name);
    }

    public void issueCommand(RemoveVehicleCommand command) throws Exception {
        // Valid. Resolve.
        IEvent event = new RemoveVehicleEvent(command.name());
        this.eventStoreService.raiseEvent(event, command.name());
    }
}
