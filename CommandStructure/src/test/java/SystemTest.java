import org.example.commandhandler.VehicleCommandHandler;
import org.example.commands.CreateVehicleCommand;
import org.example.commands.MoveVehicleCommand;
import org.example.commands.RemoveVehicleCommand;
import org.example.domainmodel.Position;
import org.example.querymodel.Projector;
import org.example.querymodel.QueryHandler;
import org.example.querymodel.VehicleDTO;
import org.example.services.HttpServerService;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

/* System test for the messaging system.
 * Send messages in and check, if the correct messages are returned.
*/
public class SystemTest {
    Projector projector = Projector.getInstance();  // Start projector
    HttpServerService httpServerService = new HttpServerService();
    VehicleCommandHandler commandHandler = VehicleCommandHandler.getInstance();
    QueryHandler queryHandler = QueryHandler.getInstance();

    @Test
    public void insertAndGetVehicles() throws Exception {
        // Defines how long timeout between commands and queries should be
        // To allow events to propagate through the queue
        int timeoutMillis = 100;

        // Create a new vehicle
        String testName = "aVehicle";
        Position testPosition = new Position(0, 0);
        this.commandHandler.issueCommand(new CreateVehicleCommand(testName, testPosition));

        // Assert new Vehicle has been correctly created and projected to the query side
        Thread.sleep(timeoutMillis);
        // Test getting vehicle by name
        VehicleDTO returnedVehicleByName = this.queryHandler.getVehicleByName(testName);
        assertEquals(returnedVehicleByName.name(), testName);
        assertEquals(returnedVehicleByName.position(), testPosition);
        // Test getting vehicle by position
        Collection<VehicleDTO> returnedVehicleByPosition = this.queryHandler.getVehiclesAtPosition(new Position(0, 0));
        assertEquals(returnedVehicleByPosition.size(), 1);
        assertEquals(returnedVehicleByPosition.iterator().next().name(), testName);

        // Move the vehicle
        this.commandHandler.issueCommand(new MoveVehicleCommand(testName, new Position(2, 1)));
        Thread.sleep(timeoutMillis);
        // Assert vehicle has been moved
        returnedVehicleByName = this.queryHandler.getVehicleByName(testName);
        assertEquals(returnedVehicleByName.name(), testName);
        assertEquals(returnedVehicleByName.position(), new Position(2, 1));

        // Add another vehicle at the same position
        this.commandHandler.issueCommand(new CreateVehicleCommand("anotherVehicle", new Position(2, 1)));
        Thread.sleep(timeoutMillis);
        // Assert there are now 2 vehicles at the position
        returnedVehicleByPosition = this.queryHandler.getVehiclesAtPosition(new Position(2, 1));
        assertEquals(returnedVehicleByPosition.size(), 2);
        // Assert "GetAllVehicles" works as well
        Collection<VehicleDTO> returnedVehicles = this.queryHandler.getVehicles();
        assertEquals(returnedVehicles.size(), 2);

        // Remove vehicles
        this.commandHandler.issueCommand(new RemoveVehicleCommand(testName));
        this.commandHandler.issueCommand(new RemoveVehicleCommand("anotherVehicle"));
        Thread.sleep(timeoutMillis);
        returnedVehicles = this.queryHandler.getVehicles();
        assertEquals(returnedVehicles.size(), 0);
    }
}
