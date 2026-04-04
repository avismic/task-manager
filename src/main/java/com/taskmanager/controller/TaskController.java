package com.taskmanager.controller;

import com.taskmanager.dto.TaskRequestDTO;
import com.taskmanager.dto.TaskResponseDTO;
import com.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    // 🔹 Create Task
    @PostMapping
    public TaskResponseDTO createTask(@Valid @RequestBody TaskRequestDTO request) {
        return taskService.createTask(request);
    }

    // 🔹 Get My Tasks
    @GetMapping
    public List<TaskResponseDTO> getMyTasks(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) String search) {

        return taskService.getMyTasks(status, completed, search);
    }

    // 🔹 Get Task by ID
    @GetMapping("/{id}")
    public TaskResponseDTO getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    // 🔹 Update Task
    @PutMapping("/{id}")
    public TaskResponseDTO updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO request) {

        return taskService.updateTask(id, request);
    }

    // 🔹 Delete Task
    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return "Task deleted successfully";
    }

    // 🔹 Toggle Complete
    @PatchMapping("/{id}/toggle")
    public TaskResponseDTO toggleTask(@PathVariable Long id) {
        return taskService.toggleTask(id);
    }
}