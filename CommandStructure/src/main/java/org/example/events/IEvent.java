package org.example.events;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateVehicleEvent.class, name = "createVehicle"),
        @JsonSubTypes.Type(value = MoveVehicleEvent.class, name = "moveVehicle"),
        @JsonSubTypes.Type(value = RemoveVehicleEvent.class, name = "removeVehicle")
})
public interface IEvent extends Serializable {
}
