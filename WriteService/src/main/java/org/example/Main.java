package org.example;
import org.example.services.HttpServerService;


public class Main {

    public static void main(String[] args) {
        HttpServerService httpServerService = new HttpServerService();
        httpServerService.startHttpServer("http://0.0.0.0:8080/api/");
        // System.out.println("Hit enter to stop the server...");
        // System.in.read();
        // httpServerService.stopHttpServer();
    }
}