package org.example;
import org.example.querymodel.Projector;
import org.example.services.HttpServerService;


public class Main {

    public static void main(String[] args) {
        Projector projector = Projector.getInstance();  // Start projector
        HttpServerService httpServerService = new HttpServerService();
        httpServerService.startHttpServer("http://0.0.0.0:8081/api/");
        // System.out.println("Hit enter to stop the server...");
        // System.in.read();
        // httpServerService.stopHttpServer();
    }
}