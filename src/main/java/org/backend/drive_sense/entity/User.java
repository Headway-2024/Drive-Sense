package org.backend.drive_sense.entity;


import io.jsonwebtoken.lang.Collections;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Email
    @NotBlank(message = "Email cannot be empty")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Size(min = 4, max = 50)
    @NotBlank(message = "Name cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Password cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "address")
    private String address;

    @Column(name = "gender")
    private String gender;

    @Column(name ="country")
    private String country;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // no roles are assigned to users
    }

    @Override
    public String getUsername() {
        return this.email;
    }
}

