package org.backend.drive_sense.service;


import org.backend.drive_sense.dto.UserDTO;
import org.backend.drive_sense.mapper.UserMapper;
import org.backend.drive_sense.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

}
