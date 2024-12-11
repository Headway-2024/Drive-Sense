package org.backend.drive_sense.repository;

import org.backend.drive_sense.entity.User;
import org.backend.drive_sense.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface VehicleRepository  extends JpaRepository<Vehicle, UUID> {
}
