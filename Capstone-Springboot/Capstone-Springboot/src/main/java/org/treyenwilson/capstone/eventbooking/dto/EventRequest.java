package org.treyenwilson.capstone.eventbooking.dto;
import jakarta.validation.constraints.*;


public class EventRequest {
    @NotBlank(message = "Make is required")
    @Size(min = 2, max = 50, message = "Make must be between 2 and 50 characters")
    private String id;
}
