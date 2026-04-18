package com.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateNameRequestDTO {

    @NotBlank(message = "Name is required")
    private String name;
}