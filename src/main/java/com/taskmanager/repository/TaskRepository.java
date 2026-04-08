package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taskmanager.entity.TaskStatus;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    List<Task> findByUserAndStatus(User user, TaskStatus status);

    List<Task> findByUserAndStatusAndCompleted(User user, TaskStatus status, boolean completed);

    List<Task> findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            User user,
            String title,
            String description);

    Page<Task> findByUser(User user, Pageable pageable);

    Page<Task> findByUserAndStatus(User user, TaskStatus status, Pageable pageable);

    Page<Task> findByUserAndStatusAndCompleted(User user, TaskStatus status, boolean completed, Pageable pageable);

    Page<Task> findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            User user,
            String title,
            String description,
            Pageable pageable);

    Page<Task> findByUserAndCompleted(User user, boolean completed, Pageable pageable);
}