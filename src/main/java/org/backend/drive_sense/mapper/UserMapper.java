package org.backend.drive_sense.mapper;

import org.backend.drive_sense.dto.UserDTO;
import org.backend.drive_sense.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDTO userToUserDTO(User user);

    User userDTOToUser(UserDTO userDTO);
}
