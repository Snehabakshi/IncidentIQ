package com.incidentiq.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTenantRequest(
        @NotBlank(message="name must not be blank")
        @Size(max=255, message = "name must not be at most 255 characters")
        String name
) {

}
