package org.backend.drive_sense.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.backend.drive_sense.entity.User;

import java.util.UUID;

@Getter
@Setter
public class VehicleDTO {

    @Id
    private UUID id;

    @Size(min = 4, max = 50)
    @NotBlank(message = "license plate cannot be empty")
    private String licensePlate;

    @Size(min = 4, max = 50)
    @NotBlank(message = "model cannot be empty")
    private String model;

    @Size(min = 4, max = 50)
    @NotBlank(message = "year cannot be empty")
    private String year;

    private UUID ownerId;
}
