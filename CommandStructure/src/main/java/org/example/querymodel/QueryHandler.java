package org.example.querymodel;
import org.example.domainmodel.Position;
import java.util.Collection;


public class QueryHandler implements Query {
    private static final QueryHandler instance = new QueryHandler();
    private final QueryModel queryModel;
    public static QueryHandler getInstance() {
        return instance;
    }

    private QueryHandler() {
        this.queryModel = QueryModel.getInstance();
    }

    @Override
    public VehicleDTO getVehicleByName(String name) {
        return this.queryModel.getVehicleByName(name);
    }

    @Override
    public Collection<VehicleDTO> getVehicles() {
        return this.queryModel.getVehicles();
    }

    @Override
    public Collection<VehicleDTO> getVehiclesAtPosition(Position position) {
        return this.queryModel.getVehicleByPosition(position);
    }
}
