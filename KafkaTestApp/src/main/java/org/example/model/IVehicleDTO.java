package org.example.model;
import org.example.model.Position;

import java.io.Serializable;

public interface IVehicleDTO extends Serializable {
    public String name();
    public Position position();
    public int numberOfMoves();
}
