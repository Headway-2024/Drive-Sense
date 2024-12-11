package org.backend.drive_sense.mapper;

import org.backend.drive_sense.dto.VehicleDTO;
import org.backend.drive_sense.entity.Vehicle;
import org.backend.drive_sense.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    @Mappings({
            @Mapping(target = "ownerId", source = "owner")
    })
    VehicleDTO vehicleToVehicleDTO(Vehicle vehicle);

    Vehicle vehicleDTOToVehicle(VehicleDTO vehicleDTO);

    default UUID mapOwnerToOwnerId(User owner) {
        return owner != null ? owner.getId() : null;
    }
}
