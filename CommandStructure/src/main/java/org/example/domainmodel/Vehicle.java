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

    public void moveVehicle(Position vector) {
        this.currentPosition = new Position(
                this.currentPosition.x() + vector.x(),
                this.currentPosition.y() + vector.y()
        );
    }
}
