package org.backend.drive_sense.mapper;

import org.backend.drive_sense.dto.UserDTO;
import org.backend.drive_sense.entity.User;
import org.backend.drive_sense.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mappings({
            @Mapping(target = "vehiclesIds", source = "vehicles")
    })
    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);

    default Set<UUID> mapVehiclesToVehicleIds(Set<Vehicle> vehicles) {
        return vehicles.stream()
                .map(Vehicle::getId)
                .collect(Collectors.toSet());
    }
}
