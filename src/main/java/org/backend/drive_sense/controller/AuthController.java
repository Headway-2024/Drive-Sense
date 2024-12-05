package org.backend.drive_sense.controller;


import org.backend.drive_sense.dto.UserDTO;
import org.backend.drive_sense.service.AuthService;
import org.backend.drive_sense.service.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v0/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<UserDTO> signup(@Valid @RequestBody UserDTO userDTO) {
        logger.info("Signing up user with email: {}", userDTO.getEmail());
        UserDTO createdUser = authService.signup(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping
    public ResponseEntity<String> hello(){
        return ResponseEntity.status(HttpStatus.CREATED).body("Hello World");
    }

}
