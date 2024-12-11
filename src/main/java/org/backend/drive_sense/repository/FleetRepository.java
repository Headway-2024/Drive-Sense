package org.backend.drive_sense.repository;

import org.backend.drive_sense.entity.Fleet;
import org.backend.drive_sense.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FleetRepository extends JpaRepository<Fleet, UUID> {
    Optional<Fleet> findByManager(User manager);

}
