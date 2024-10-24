package org.example.services;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.example.commandhandler.VehicleCommandHandler;
import org.example.commands.*;
import org.example.domainmodel.Position;
import org.glassfish.jersey.server.ResourceConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import java.net.URI;


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
}
