package org.example.querymodel;
import org.example.domainmodel.Position;

import java.io.Serializable;

public interface IVehicleDTO extends Serializable {
    public String name();
    public Position position();
    public int numberOfMoves();
}
