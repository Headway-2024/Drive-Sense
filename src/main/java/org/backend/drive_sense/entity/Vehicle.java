package org.backend.drive_sense.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;


@Getter
@Setter
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Size(min = 4, max = 50)
    @NotBlank(message = "license_plate cannot be empty")
    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Size(min = 4, max = 50)
    @NotBlank(message = "model cannot be empty")
    @Column(name = "model", nullable = false)
    private String model;

    @Size(min = 4, max = 50)
    @NotBlank(message = "year cannot be empty")
    @Column(name = "year", nullable = false)
    private String year;

    @NotNull(message = "owner cannot be empty")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "fleet_id")
    private Fleet fleet;

}
