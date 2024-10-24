package org.example.commandhandler;
import org.example.commands.CreateVehicleCommand;
import org.example.commands.ICommand;
import org.example.commands.MoveVehicleCommand;
import org.example.commands.RemoveVehicleCommand;
import org.example.domainmodel.Vehicle;
import org.example.domainmodel.VehicleRegister;
import org.example.events.CreateVehicleEvent;
import org.example.events.MoveVehicleEvent;
import org.example.events.RemoveVehicleEvent;
import org.example.services.EventStoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleCommandHandler {
    private static final VehicleCommandHandler instance = new VehicleCommandHandler();

    private final Logger logger = LoggerFactory.getLogger(VehicleCommandHandler.class);

    private final VehicleRegister vehicleRegister;
    private final EventStoreService eventStoreService;


    public VehicleCommandHandler() {
        vehicleRegister = VehicleRegister.getInstance();
        eventStoreService = EventStoreService.getInstance();
        logger.debug("Command handler initialized");
    }

    public static VehicleCommandHandler getInstance() {
        return instance;
    }

    public void issueCommand(ICommand command) throws Exception {
        if (command instanceof CreateVehicleCommand) {
            this.createVehicle((CreateVehicleCommand) command);
        } else if (command instanceof MoveVehicleCommand) {
            this.moveVehicle((MoveVehicleCommand) command);
        } else if (command instanceof RemoveVehicleCommand) {
            this.removeVehicle((RemoveVehicleCommand) command);
        }
    }

    private void createVehicle(CreateVehicleCommand command) throws Exception {
        // Check if vehicle already exists
        String name = command.getName();
        if (vehicleRegister.vehicleExists(name)) {
            throw new Exception("vehicle with name " + name + "already exists");
        }
        // Does not exist. Generate event.
        Vehicle vehicle = new Vehicle(command.getName(), command.getStartPosition());
        this.eventStoreService.raiseEvent(new CreateVehicleEvent(command.getName(), command.getStartPosition()));

        // Change local domain model. TODO: change this to be inferred from event store
        this.vehicleRegister.createVehicle(vehicle.name, vehicle);
    }

    private void moveVehicle(MoveVehicleCommand command) throws Exception {
        // Check validity
        if (command.getVector() == null || command.getVector().x() == 0 && command.getVector().y() == 0) {
            throw new Exception("invalid vector");
        }
        // Valid. Resolve.
        this.eventStoreService.raiseEvent(new MoveVehicleEvent(command.getName(), command.getVector()));

        // Change local domain model. TODO: change this to be inferred from event store
        this.vehicleRegister.moveVehicle(command.getName(), command.getVector());
    }

    private void removeVehicle(RemoveVehicleCommand command) throws Exception {
        // TODO: Checks?

        // Valid. Resolve.
        this.eventStoreService.raiseEvent(new RemoveVehicleEvent(command.getName()));

        // Change local domain model. TODO: change this to be inferred from event store
        this.vehicleRegister.deleteVehicle(command.getName());
    }
}
