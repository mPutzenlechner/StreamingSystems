import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.model.CreateVehicleCommand;
import org.example.model.MoveVehicleCommand;
import org.example.model.Position;
import org.example.model.VehicleDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class KafkaTest {

    private final String writeSideUrl = "http://localhost:8080/api/commands";
    private final String querySideUrl = "http://localhost:8081/api/commands";
    private final int waitInterval = 100;

    @Test
    void test() throws InterruptedException, JsonProcessingException {
        // Create client
        ObjectReader objectReader = new ObjectMapper().readerFor(VehicleDTO.class);
        Client client = ClientBuilder.newClient();

        // Create a vehicle and do some tests with it.
        Position startPosition = new Position(100, 100);
        CreateVehicleCommand createCommand = new CreateVehicleCommand("junit", startPosition);
        Response response = postWithResponse(
                client,
                writeSideUrl + "/new/"
                        + createCommand.name() + "/"
                        + createCommand.startPosition().x() + "/"
                        + createCommand.startPosition().y()
        );
        assertEquals(200, response.getStatus());
        Thread.sleep(waitInterval);
        response = getWebResponse(client, querySideUrl  + "/byname/" + createCommand.name());
        String vehicleJson = response.readEntity(String.class);
        VehicleDTO vehicle = objectReader.readValue(vehicleJson);

        // Assert vehicle fetched via query equals vehicle sent
        assertEquals(vehicle.name(), createCommand.name());
        assertEquals(vehicle.position().x(), startPosition.x());
        assertEquals(vehicle.position().y(), startPosition.y());

        // Move vehicle
        Position vector = new Position(50, 50);
        MoveVehicleCommand moveCommand = new MoveVehicleCommand("junit", vector);

        response = postWithResponse(
                client,
                writeSideUrl + "/move/"
                        + moveCommand.name() + "/"
                        + moveCommand.vector().x() + "/"
                        + moveCommand.vector().y()
        );
        assertEquals(200, response.getStatus());
        Thread.sleep(waitInterval);
        response = getWebResponse(client, querySideUrl  + "/byname/" + createCommand.name());
        vehicleJson = response.readEntity(String.class);
        vehicle = objectReader.readValue(vehicleJson);

        // Assert vehicle fetched via query equals vehicle sent
        assertEquals(vehicle.name(), moveCommand.name());
        assertEquals(vehicle.position().x(), startPosition.x() + vector.x());
        assertEquals(vehicle.position().y(), startPosition.y() + vector.y());

        // Remove vehicle
        response = postWithResponse(
                client,
                writeSideUrl + "/remove/"
                        + moveCommand.name()
        );
        assertEquals(200, response.getStatus());
    }

    private Response getWebResponse(Client client, String target) {
        return client
                .target(target)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get();
    }

    private Response postWithResponse(Client client, String target) {
        return client
                .target(target)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .post(Entity.entity("", MediaType.APPLICATION_JSON));
    }
}
