package org.example.services;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.example.commandhandler.VehicleCommandHandler;
import org.example.commands.CreateVehicleCommand;
import org.example.commands.MoveVehicleCommand;
import org.example.commands.RemoveVehicleCommand;
import org.example.domainmodel.Position;

@Path("commands")
public class HttpServer {

    @POST
    @Path("/new/{name}/{x}/{y}")
    public Response createVehicle(@PathParam("name") String name, @PathParam("x") int x, @PathParam("y") int y ) {
        CreateVehicleCommand command = new CreateVehicleCommand(name, new Position(x, y));
        try {
            VehicleCommandHandler.getInstance().issueCommand(command);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/move/{name}/{x}/{y}")
    public Response moveVehicle(@PathParam("name") String name, @PathParam("x") int x, @PathParam("y") int y ) {
        MoveVehicleCommand command = new MoveVehicleCommand(name, new Position(x, y));
        try {
            VehicleCommandHandler.getInstance().issueCommand(command);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/remove/{name}")
    public Response removeVehicle(@PathParam("name") String name) {
        RemoveVehicleCommand command = new RemoveVehicleCommand(name);
        try {
            VehicleCommandHandler.getInstance().issueCommand(command);
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }
}
