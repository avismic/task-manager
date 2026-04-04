package com.taskmanager.repository;

import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import com.taskmanager.entity.TaskStatus;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByUser(User user);

    List<Task> findByUserAndStatus(User user, TaskStatus status);

    List<Task> findByUserAndStatusAndCompleted(User user, TaskStatus status, boolean completed);

    List<Task> findByUserAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            User user,
            String title,
            String description);
}