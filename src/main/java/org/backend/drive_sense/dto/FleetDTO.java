package org.backend.drive_sense.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class FleetDTO {

    @Id
    private UUID id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private UUID managerId;
    private Set<UUID> vehicleIds;
}
