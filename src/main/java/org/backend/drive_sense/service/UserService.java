package org.backend.drive_sense.service;



import java.util.Set;
import java.util.UUID;

import org.backend.drive_sense.dto.UserDTO;
import org.backend.drive_sense.entity.User;
import org.backend.drive_sense.mapper.UserMapper;
import org.backend.drive_sense.repository.UserRepository;
import org.mapstruct.control.MappingControl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Validator;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import jakarta.validation.ConstraintViolation;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.backend.drive_sense.utils.PasswordValidator.validateRawPassword;
import static org.backend.drive_sense.utils.ValidationUtils.getValidationErrors;

@Service
@Transactional
public class UserService {

    private final Validator validator;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public UserService(Validator validator, UserMapper userMapper, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.validator = validator;
        this.userMapper = userMapper;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO createUser(UserDTO userDTO) {
        logger.debug("Validating user for creation: {}", userDTO);
        if (userDTO == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User already exists");
        }

        Set<ConstraintViolation<UserDTO>> violations = validator.validate(userDTO);
        if (!violations.isEmpty()) {
            logger.error("Validation errors: {}", getValidationErrors(violations));
            String validationErrors = getValidationErrors(violations);
            throw new IllegalArgumentException("Validation errors: " + validationErrors);
        }

        validateRawPassword(userDTO.getPassword());

        User user = userMapper.userDTOToUser(userDTO);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User created with ID: {}", savedUser.getId());
        return userMapper.userToUserDTO(user);
    }

    public Page<UserDTO> getAllUsers(int limit, int offset) {
        logger.debug("Fetching all users with limit: {}, offset: {}", limit, offset);
        if (limit < 1 || offset < 0) {
            throw new IllegalArgumentException("Limit must be greater than 0 and offset must be non-negative.");
        }
        Pageable pageable = PageRequest.of(offset / limit, limit);
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::userToUserDTO);
    }

    public UserDTO getUserById(UUID id) {
        logger.debug("Fetching user with ID: {}", id);
        User user =  userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return userMapper.userToUserDTO(user);
    }

    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        logger.debug("Updating user with ID: {}", id);
        if (userDTO == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        return userRepository.findById(id)
                .map(existingUser -> {
                    if (userDTO.getEmail() != null) {
                        Set<ConstraintViolation<UserDTO>> violations = validator.validateProperty(userDTO, "email");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setEmail(userDTO.getEmail());
                    }
                    if (userDTO.getName() != null) {
                        Set<ConstraintViolation<UserDTO>> violations = validator.validateProperty(userDTO, "name");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setName(userDTO.getName());
                    }
                    if (userDTO.getPassword() != null && !userDTO.getPassword().isEmpty()) {
                        validateRawPassword(userDTO.getPassword());
                        existingUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
                    }
                    if(userDTO.getCountry()!=null){
                        Set<ConstraintViolation<UserDTO>> violations = validator.validateProperty(userDTO , "country");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setCountry(userDTO.getCountry());
                    }
                    if(userDTO.getGender()!=null){
                        Set<ConstraintViolation<UserDTO>> violations = validator.validateProperty(userDTO , "gender");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setGender(userDTO.getGender());
                    }
                    if(userDTO.getAddress()!=null){
                        Set<ConstraintViolation<UserDTO>> violations = validator.validateProperty(userDTO , "address");
                        if (!violations.isEmpty()) {
                            logger.error("Validation errors: {}", getValidationErrors(violations));
                            throw new IllegalArgumentException("Validation errors: " + getValidationErrors(violations));
                        }
                        existingUser.setAddress(userDTO.getAddress());
                    }
                    return userRepository.save(existingUser);
                })
                .map(userMapper::userToUserDTO)
                .orElseThrow(() -> {
                    logger.error("User not found with ID: {}", id);
                    return new IllegalArgumentException("User not found");
                });
    }

    public void deleteUser(UUID id) {
        logger.debug("Deleting user with ID: {}", id);
        if (!userRepository.existsById(id)) {
            logger.error("User not found with ID: {}", id);
            throw new IllegalArgumentException("User not found");
        }
        userRepository.deleteById(id);
        logger.info("User deleted with ID: {}", id);
    }
}

