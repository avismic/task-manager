package com.taskmanager.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private String status;
}