package com.incidentiq.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record RegisterRequest(
        @NotNull(message="tenantId is required")
        UUID tenantId,

        @NotBlank(message="email must not be blank")
        @Email(message="email must be a valid email address")
        @Size(max=255 , message="email must be at most 255 characters")
        String email,

        @NotBlank(message="password must not be blank")
        @Size(min=8 , max=72 , message="Password must be between 8 and 72 characters")
        String password
) {

}
