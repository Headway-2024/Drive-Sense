package org.backend.drive_sense.service;

import org.backend.drive_sense.dto.VehicleDTO;
import org.backend.drive_sense.entity.User;
import org.backend.drive_sense.entity.Vehicle;
import org.backend.drive_sense.mapper.VehicleMapper;
import org.backend.drive_sense.repository.UserRepository;
import org.backend.drive_sense.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.util.Set;
import java.util.UUID;

import static org.backend.drive_sense.utils.UserUtils.getCurrentUserEmail;
import static org.backend.drive_sense.utils.ValidationUtils.getValidationErrors;

@Service
@Transactional
public class VehicleService {

    private final Validator validator;
    private final VehicleMapper vehicleMapper;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    public VehicleService(Validator validator, VehicleMapper vehicleMapper,
                          VehicleRepository vehicleRepository, UserRepository userRepository) {
        this.validator = validator;
        this.vehicleMapper = vehicleMapper;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
    }

    public VehicleDTO createVehicle(VehicleDTO vehicleDTO) {
        logger.debug("Validating vehicle for creation: {}", vehicleDTO);

        if (vehicleDTO == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }
        UUID userId;
        if(vehicleDTO.getOwnerId()==null){
            String email = getCurrentUserEmail();
            userId = userRepository.findByEmail(email).get().getId();
        }
        else{
            userId = vehicleDTO.getOwnerId();
        }

        logger.info("Creating vehicle for user with ID: {}", userId);

        // Validate the DTO
        Set<ConstraintViolation<VehicleDTO>> violations = validator.validate(vehicleDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
        }

        // Retrieve the user entity from the repository
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));

        // Map DTO to entity
        Vehicle vehicle = vehicleMapper.vehicleDTOToVehicle(vehicleDTO);
        vehicle.setOwner(owner);

        // Save the vehicle entity
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        owner.getVehicles().add(savedVehicle);
        userRepository.save(owner);
        logger.info("Vehicle created with ID: {}", savedVehicle.getId());

        return vehicleMapper.vehicleToVehicleDTO(savedVehicle);
    }




    public Page<VehicleDTO> getAllVehicles(int limit, int offset) {
        logger.debug("Fetching all vehicles with limit: {}, offset: {}", limit, offset);
        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<Vehicle> vehicles = vehicleRepository.findAll(pageable);
        return vehicles.map(vehicleMapper::vehicleToVehicleDTO);
    }

    public VehicleDTO getVehicleById(UUID id) {
        logger.debug("Fetching vehicle with ID: {}", id);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));
        return vehicleMapper.vehicleToVehicleDTO(vehicle);
    }

    public VehicleDTO updateVehicle(UUID id, VehicleDTO vehicleDTO) {
        logger.debug("Updating vehicle with ID: {}", id);
        if (vehicleDTO == null) {
            throw new IllegalArgumentException("Vehicle cannot be null");
        }

        return vehicleRepository.findById(id)
                .map(existingVehicle -> {
                    if (vehicleDTO.getLicensePlate() != null) {
                        validateField("licensePlate", vehicleDTO.getLicensePlate(), vehicleDTO);
                        existingVehicle.setLicensePlate(vehicleDTO.getLicensePlate());
                    }
                    if (vehicleDTO.getModel() != null) {
                        validateField("model", vehicleDTO.getModel(), vehicleDTO);
                        existingVehicle.setModel(vehicleDTO.getModel());
                    }
                    if (vehicleDTO.getYear() != null) {
                        validateField("year", vehicleDTO.getYear(), vehicleDTO);
                        existingVehicle.setYear(vehicleDTO.getYear());
                    }
                    if (vehicleDTO.getOwnerId() != null) {
                        User newOwner = userRepository.findById(vehicleDTO.getOwnerId())
                                .orElseThrow(() -> new IllegalArgumentException("Owner not found"));
                        existingVehicle.setOwner(newOwner);
                    }
                    return vehicleRepository.save(existingVehicle);
                })
                .map(vehicleMapper::vehicleToVehicleDTO)
                .orElseThrow(() -> {
                    logger.error("Vehicle not found with ID: {}", id);
                    return new IllegalArgumentException("Vehicle not found");
                });
    }

    public void deleteVehicle(UUID id) {
        logger.debug("Deleting vehicle with ID: {}", id);
        if (!vehicleRepository.existsById(id)) {
            logger.error("Vehicle not found with ID: {}", id);
            throw new IllegalArgumentException("Vehicle not found");
        }
        vehicleRepository.deleteById(id);
        logger.info("Vehicle deleted with ID: {}", id);
    }

    private void validateField(String fieldName, String fieldValue, VehicleDTO vehicleDTO) {
        Set<ConstraintViolation<VehicleDTO>> violations = validator.validateProperty(vehicleDTO, fieldName);
        if (!violations.isEmpty()) {
            logger.error("Validation errors for {}: {}", fieldName, getValidationErrors(violations));
            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
        }
    }
}
