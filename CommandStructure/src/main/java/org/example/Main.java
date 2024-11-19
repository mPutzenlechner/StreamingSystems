package org.example;
import org.example.querymodel.Projector;
import org.example.services.HttpServerService;
import java.io.IOException;


public class Main {

    public static void main(String[] args) throws IOException {
        System.setProperty(org.slf4j.simple.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "Debug");
        Projector projector = Projector.getInstance();  // Start projector
        HttpServerService httpServerService = new HttpServerService();
        httpServerService.startHttpServer("http://localhost:8080/api/");
        // System.out.println("Hit enter to stop the server...");
        // System.in.read();
        // httpServerService.stopHttpServer();
    }
}