package org.example.domainmodel;
import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    public String name;
    public Position startPosition;
    public Position currentPosition;
    private final List<Position> positionHistory = new ArrayList<>();

    public Vehicle(String name, Position startPosition) {
        this.name = name;
        this.startPosition = startPosition;
        this.currentPosition = startPosition;
        positionHistory.add(startPosition);
    }

    public void move(Position vector) {
        this.currentPosition = new Position(
                this.currentPosition.x() + vector.x(),
                this.currentPosition.y() + vector.y()
        );
        positionHistory.add(this.currentPosition);
    }

    public List<Position> getPositionHistory() {
        return positionHistory;
    }

    public int getNumberOfMoves() {
        return positionHistory.size();
    }
}
