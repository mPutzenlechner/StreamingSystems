package org.example.services;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.example.commandhandler.VehicleCommandHandler;
import org.example.commands.*;
import org.example.domainmodel.Position;
import org.example.querymodel.QueryHandler;
import org.example.querymodel.VehicleDTO;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;
import java.util.Collection;


@Path("commands")
public class HttpServerService {

    private HttpServer server;

    public void startHttpServer(String baseUrl) {
        final ResourceConfig rc = new ResourceConfig().packages("org.example");

        this.server = GrizzlyHttpServerFactory.createHttpServer(URI.create(baseUrl), rc);
    }

    public void stopHttpServer() {
        this.server.shutdown();
    }

    private final Logger logger = LoggerFactory.getLogger(HttpServerService.class);

    @POST
    @Path("/new/{name}/{x}/{y}")
    public Response createVehicle(@PathParam("name") String name, @PathParam("x") int x, @PathParam("y") int y ) {
        CreateVehicleCommand command = new CreateVehicleCommand(name, new Position(x, y));
        try {
            VehicleCommandHandler.getInstance().issueCommand(command);
        } catch (Exception e) {
            this.logger.error("HttpRequest \"new vehicle\" failed with error: {}", e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
        this.logger.debug("Command created for HttpRequest \"new vehicle\".");
        return Response.ok().build();
    }

    @POST
    @Path("/move/{name}/{x}/{y}")
    public Response moveVehicle(@PathParam("name") String name, @PathParam("x") int x, @PathParam("y") int y ) {
        MoveVehicleCommand command = new MoveVehicleCommand(name, new Position(x, y));
        try {
            VehicleCommandHandler.getInstance().issueCommand(command);
        } catch (Exception e) {
            this.logger.error("HttpRequest \"move vehicle\" failed with error: {}", e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
        this.logger.debug("Command created for HttpRequest \"move vehicle\".");
        return Response.ok().build();
    }

    @POST
    @Path("/remove/{name}")
    public Response removeVehicle(@PathParam("name") String name) {
        RemoveVehicleCommand command = new RemoveVehicleCommand(name);
        try {
            VehicleCommandHandler.getInstance().issueCommand(command);
        } catch (Exception e) {
            this.logger.error("HttpRequest \"remove vehicle\" failed with error: {}", e.getMessage());
            return Response.serverError().entity(e.getMessage()).build();
        }
        this.logger.debug("Command created for HttpRequest \"remove vehicle\".");
        return Response.ok().build();
    }

    @GET
    @Path("/byname/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getVehicle(@PathParam("name") String name) {
        try {
            VehicleDTO vehicleDTO = QueryHandler.getInstance().getVehicleByName(name);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(vehicleDTO);
        } catch (Exception e) {
            this.logger.error("HttpRequest \"get vehicle\" failed with error: {}", e.getMessage());
            return e.getMessage();
        }
    }

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllVehicles() {
        try {
            Collection<VehicleDTO> vehicleDTOCollection = QueryHandler.getInstance().getVehicles();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(vehicleDTOCollection);
        } catch (Exception e) {
            this.logger.error("HttpRequest \"get vehicles\" failed with error: {}", e.getMessage());
            return e.getMessage();
        }
    }

    @GET
    @Path("/at/{x}/{y}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getVehiclesAtPosition(@PathParam("x") int x, @PathParam("y") int y) {
        try {
            Collection<VehicleDTO> vehicleDTOCollection = QueryHandler.getInstance().getVehiclesAtPosition(
                    new Position(x, y)
            );
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(vehicleDTOCollection);
        } catch (Exception e) {
            this.logger.error("HttpRequest \"get vehicles at position\" failed with error: {}", e.getMessage());
            return e.getMessage();
        }
    }
}
