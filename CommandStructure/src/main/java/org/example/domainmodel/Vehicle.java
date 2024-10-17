package org.example.domainmodel;


public class Vehicle {
    public String name;
    public Position startPosition;
    public Position currentPosition;

    public Vehicle(String name, Position startPosition) {
        this.name = name;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
    }
}
