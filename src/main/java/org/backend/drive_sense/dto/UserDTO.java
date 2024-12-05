package org.backend.drive_sense.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDTO {

    @Id
    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    private String email;

    @Size(min = 4, max = 50)
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    private String accessToken;

    private String refreshToken;
}
