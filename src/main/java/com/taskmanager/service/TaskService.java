package com.taskmanager.service;

import com.taskmanager.dto.TaskRequestDTO;
import com.taskmanager.dto.TaskResponseDTO;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.exception.UnauthorizedException;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    // 🔹 Create Task
    public TaskResponseDTO createTask(TaskRequestDTO request) {

        User user = getCurrentUser();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .completed(false)
                .status(com.taskmanager.entity.TaskStatus.TODO)
                .user(user)
                .build();

        Task savedTask = taskRepository.save(task);

        return mapToResponse(savedTask);
    }

    // 🔹 Update Task
    public TaskResponseDTO updateTask(Long id, TaskRequestDTO request) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to access this resource");
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
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to access this resource");
        }

        taskRepository.delete(task);
    }

    // 🔹 Toggle Task Completion
    public TaskResponseDTO toggleTask(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to access this resource");
        }

        // 🔄 Toggle
        task.setCompleted(!task.isCompleted());

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    // 🔹 Get all tasks of logged-in user
    public List<TaskResponseDTO> getMyTasks(int page, int size, String status, Boolean completed, String search) {

        User user = getCurrentUser();

        Pageable pageable = PageRequest.of(page, size);

        Page<Task> taskPage;

        // 🔍 Case 1: Search (highest priority)
        if (search != null && !search.isBlank()) {
            taskPage = taskRepository
                    .findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                            user,
                            search,
                            search,
                            pageable);
        }

        // Case 2: status + completed
        else if (status != null && completed != null) {
            taskPage = taskRepository.findByUserAndStatusAndCompleted(
                    user,
                    com.taskmanager.entity.TaskStatus.valueOf(status.toUpperCase()),
                    completed,
                    pageable);
        }

        // Case 3: only status
        else if (status != null) {
            taskPage = taskRepository.findByUserAndStatus(
                    user,
                    com.taskmanager.entity.TaskStatus.valueOf(status.toUpperCase()),
                    pageable);
        }

        // Case 4: only completed
        else if (completed != null) {
            taskPage = taskRepository.findByUserAndCompleted(user, completed, pageable);
        }

        // Case 5: no filters
        else {
            taskPage = taskRepository.findByUser(user, pageable);
        }

        return taskPage.getContent()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // 🔹 Get single task by ID (with ownership check)
    public TaskResponseDTO getTaskById(Long id) {

        User user = getCurrentUser();

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

        // 🔒 Ownership check
        if (!task.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You are not allowed to access this resource");
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
                .status(task.getStatus().name())
                .build();
    }
}