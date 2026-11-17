package com.incidentiq.backend.dto;

import com.incidentiq.backend.entity.FeedbackRating;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FeedbackRequest(
        @NotNull FeedbackRating rating,
        @Size(max=1000) String comment
        ) {
}
