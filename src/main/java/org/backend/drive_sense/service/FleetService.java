package org.backend.drive_sense.service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.backend.drive_sense.dto.FleetDTO;
import org.backend.drive_sense.entity.Fleet;
import org.backend.drive_sense.entity.User;
import org.backend.drive_sense.entity.Vehicle;
import org.backend.drive_sense.mapper.FleetMapper;
import org.backend.drive_sense.repository.FleetRepository;
import org.backend.drive_sense.repository.UserRepository;
import org.backend.drive_sense.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.backend.drive_sense.utils.UserUtils.getCurrentUserEmail;
import static org.backend.drive_sense.utils.ValidationUtils.getValidationErrors;

@Service
@Transactional
public class FleetService {

    private final FleetRepository fleetRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final FleetMapper fleetMapper;
    private static final Logger logger = LoggerFactory.getLogger(FleetService.class);
    private final Validator validator;

    public FleetService(FleetRepository fleetRepository, VehicleRepository vehicleRepository,
                        UserRepository userRepository, FleetMapper fleetMapper , Validator validator) {
        this.fleetRepository = fleetRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.fleetMapper = fleetMapper;
        this.validator = validator;
    }

    public FleetDTO createFleet(FleetDTO fleetDTO) {
        logger.debug("Validating fleet for creation: {}", fleetDTO);

        if (fleetDTO == null) {
            throw new IllegalArgumentException("Fleet cannot be null");
        }

        UUID managerId;
        if (fleetDTO.getManagerId() == null) {
            String email = getCurrentUserEmail();
            managerId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Manager not found")).getId();
        } else {
            managerId = fleetDTO.getManagerId();
        }

        logger.info("Creating fleet for manager with ID: {}", managerId);

        // Validate the DTO
        Set<ConstraintViolation<FleetDTO>> violations = validator.validate(fleetDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
        }

        // Retrieve the manager entity from the repository
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        // Map DTO to entity
        Fleet fleet = fleetMapper.fleetDTOToFleet(fleetDTO);
        fleet.setManager(manager);

        // Save the fleet entity
        Fleet savedFleet = fleetRepository.save(fleet);

        logger.info("Fleet created with ID: {}", savedFleet.getId());

        return fleetMapper.fleetToFleetDTO(savedFleet);
    }


    public FleetDTO assignVehicleToFleet(UUID fleetId, UUID vehicleId) {
        Fleet fleet = fleetRepository.findById(fleetId)
                .orElseThrow(() -> new IllegalArgumentException("Fleet not found"));

        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        vehicle.setFleet(fleet);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        fleet.getVehicles().add(savedVehicle);
        fleetRepository.save(fleet);
        return fleetMapper.fleetToFleetDTO(fleet);
    }

    public FleetDTO getFleetByManager(UUID managerId) {
        User manager = userRepository.findById(managerId)
                .orElseThrow(() -> new IllegalArgumentException("Manager not found"));

        Fleet fleet = fleetRepository.findByManager(manager)
                .orElseThrow(() -> new IllegalArgumentException("Fleet not found for the manager"));

        return fleetMapper.fleetToFleetDTO(fleet);
    }



}
