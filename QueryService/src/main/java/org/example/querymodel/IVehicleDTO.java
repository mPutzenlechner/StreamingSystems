package org.example.querymodel;
import org.example.querymodel.Position;

import java.io.Serializable;

public interface IVehicleDTO extends Serializable {
    public String name();
    public Position position();
    public int numberOfMoves();
}
