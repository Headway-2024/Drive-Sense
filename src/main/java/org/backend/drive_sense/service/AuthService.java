package org.backend.drive_sense.service;


import org.backend.drive_sense.dto.UserDTO;
import org.backend.drive_sense.entity.User;
import org.backend.drive_sense.mapper.UserMapper;
import org.backend.drive_sense.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final static Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(UserMapper userMapper,
                       UserService userService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider) {
        this.userMapper = userMapper;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }


    public UserDTO signup(UserDTO userDTO) {
        logger.info("Signing up new user with email: {}", userDTO.getEmail());
        UserDTO createdUser = userService.createUser(userDTO);
        String accessToken = jwtTokenProvider.generateAccessToken(userDTO.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDTO.getEmail());

        createdUser.setAccessToken(accessToken);
        createdUser.setRefreshToken(refreshToken);

        userRepository.save(userMapper.userDTOToUser(createdUser));
        logger.info("User signed up successfully with email: {}", userDTO.getEmail());

        return createdUser;
    }

    public UserDTO login(String email, String password) {
        logger.info("Attempting to log in user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.warn("Login failed: Incorrect password for email: {}", email);
            throw new IllegalArgumentException("Wrong password");
        }

        String accessToken = jwtTokenProvider.generateAccessToken(email);
        String refreshToken = jwtTokenProvider.generateRefreshToken(email);

        user.setAccessToken(accessToken);
        user.setRefreshToken(refreshToken);

        userRepository.save(user);
        logger.info("User logged in successfully with email: {}", email);

        return userMapper.userToUserDTO(user);
    }

    public void logout(String id) {
        logger.info("Logging out user with ID: {}", id);
        // TODO: implement method logic.
        logger.info("User with ID: {} has been logged out", id);
    }

    public String generateAccessToken(String refreshToken) {
        logger.info("Generating new access token from refresh token");
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            logger.error("Invalid refresh token provided");
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String email = jwtTokenProvider.getEmailFromToken(refreshToken);
        String accessToken = jwtTokenProvider.generateAccessToken(email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setAccessToken(accessToken);

        userRepository.save(user);
        logger.info("New access token generated and saved for user with email: {}", email);

        return accessToken;
    }


}
