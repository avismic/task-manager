package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequestDTO;
import com.taskmanager.dto.TaskResponseDTO;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Task Management", description = "APIs for managing user tasks")
public class TaskController {

    private final TaskService taskService;

    // 🔹 Create Task
    @Operation(summary = "Create a new task", description = "Creates a task for the logged-in user")
    @PostMapping
    public TaskResponseDTO createTask(@Valid @RequestBody TaskRequestDTO request) {
        return taskService.createTask(request);
    }

    // 🔹 Get My Tasks
    @Operation(summary = "Get all tasks", description = "Fetch tasks with optional filters (status, completed, search)")
    @GetMapping
    public List<TaskResponseDTO> getMyTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String search) {

        return taskService.getMyTasks(status, completed, search);
    }

    // 🔹 Get Task by ID
    @Operation(summary = "Get task by ID", description = "Fetch a specific task by its ID")
    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // 🔹 Update Task
    @Operation(summary = "Update task", description = "Update task details by ID")
    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {

        return taskService.updateTask(id, request);
    }

    // 🔹 Delete Task
    @Operation(summary = "Delete task", description = "Delete a task by ID")
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "Task deleted successfully";
    }

    // 🔹 Toggle Complete
    @Operation(summary = "Toggle task completion", description = "Mark task as completed or incomplete")
    @PatchMapping("/{id}/toggle")
    public TaskResponseDTO toggleTask(@PathVariable Long id) {
        return taskService.toggleTask(id);
    }
}