package com.taskmanager.service;

import com.taskmanager.dto.TaskRequestDTO;
import com.taskmanager.dto.TaskResponseDTO;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    // 🔹 Get logged-in user
    private User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        var userDetails = (com.taskmanager.security.CustomUserDetails) authentication.getPrincipal();

        return userRepository.findByEmail(userDetails.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // 🔹 Create Task
    public TaskResponseDTO createTask(TaskRequestDTO request) {

        User user = getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    // 🔹 Update Task
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        // 🔄 Update fields
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    // 🔹 Delete Task
    public void deleteTask(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        taskRepository.delete(task);
    }

    // 🔹 Toggle Task Completion
    public TaskResponseDTO toggleTask(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        // 🔄 Toggle
        task.setCompleted(!task.isCompleted());

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    // 🔹 Get all tasks of logged-in user
    public List<TaskResponseDTO> getMyTasks() {

        User user = getCurrentUser();

        return taskRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get single task by ID (with ownership check)
    public TaskResponseDTO getTaskById(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Unauthorized access");
        }

        return mapToResponse(task);
    }

    // 🔹 Mapper
    private TaskResponseDTO mapToResponse(Task task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .completed(task.isCompleted())
                .build();
    }
}