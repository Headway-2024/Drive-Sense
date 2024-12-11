package org.backend.drive_sense.mapper;

import org.backend.drive_sense.dto.FleetDTO;
import org.backend.drive_sense.entity.Fleet;
import org.backend.drive_sense.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {VehicleMapper.class})
public interface FleetMapper {

    @Mappings({
            @Mapping(target = "managerId", source = "manager.id"),
            @Mapping(target = "vehicleIds", source = "vehicles")
    })
    FleetDTO fleetToFleetDTO(Fleet fleet);

    @Mappings({
            @Mapping(target = "manager.id", source = "managerId"),
            @Mapping(target = "vehicles", ignore = true) // Vehicles can be set explicitly later
    })
    Fleet fleetDTOToFleet(FleetDTO fleetDTO);

    default Set<UUID> mapVehiclesToVehicleIds(Set<Vehicle> vehicles) {
        if (vehicles == null) {
            return Set.of(); // Return an empty set if vehicles is null
        }
        return vehicles.stream()
                .map(Vehicle::getId)
                .collect(Collectors.toSet());
    }
}
