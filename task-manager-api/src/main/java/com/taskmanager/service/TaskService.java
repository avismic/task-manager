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
import org.springframework.data.domain.Sort;

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
        switch (task.getStatus()) {
            case TODO:
                task.setStatus(com.taskmanager.entity.TaskStatus.IN_PROGRESS);
                task.setCompleted(false);
                break;

            case IN_PROGRESS:
                task.setStatus(com.taskmanager.entity.TaskStatus.DONE);
                task.setCompleted(true);
                break;

            case DONE:
                task.setStatus(com.taskmanager.entity.TaskStatus.TODO);
                task.setCompleted(false);
                break;
        }

        Task updatedTask = taskRepository.save(task);

        return mapToResponse(updatedTask);
    }

    // 🔹 Get all tasks of logged-in user
    public Page<TaskResponseDTO> getMyTasks(int page, int size, String sort, String status, Boolean completed,
            String search) {

        User user = getCurrentUser();

        String[] sortParts = sort.split(",");

        String field = sortParts[0];
        String direction = sortParts.length > 1 ? sortParts[1] : "asc";

        Sort.Direction dir = direction.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(dir, field));
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

        return taskPage.map(this::mapToResponse);
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