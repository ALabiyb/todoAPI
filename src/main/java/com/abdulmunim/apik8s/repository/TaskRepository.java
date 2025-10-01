package com.abdulmunim.apik8s.repository;

import com.abdulmunim.apik8s.model.Priority;
import com.abdulmunim.apik8s.model.Status;
import com.abdulmunim.apik8s.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // Find tasks by completion status
//    List<Task> findByCompleted(Boolean completed);

    // Find tasks by priority
    List<Task> findByPriority(Priority priority);

    // Find tasks ordered by priority (descending - URGENT first)
    List<Task> findByOrderByPriorityDesc();

    // Find tasks ordered by priority (ascending - LOW first)
    List<Task> findByOrderByPriorityAsc();

    // Find tasks by completion status and priority
//    List<Task> findByCompletedAndPriority(Boolean completed, Priority priority);

    // Find tasks by title containing text (case insensitive)
    List<Task> findByTitleContainingIgnoreCase(String title);

    // Find tasks ordered by creation date
    List<Task> findByOrderByCreatedAtDesc();

    List<Task> findByOrderByCreatedAtAsc();

    // Find tasks by status
    List<Task> findByStatus(Status status);

    // Find all tasks ordered by status (optional)
    List<Task> findAllByOrderByStatusAsc();

    // Count by status
    long countByStatus(Status status);

    // Custom query to find urgent incomplete tasks
//    @Query("SELECT t FROM Task t WHERE t.completed = false AND t.priority = 'URGENT'")
//    List<Task> findUrgentIncompleteTasks();

    @Query("SELECT t FROM Task t WHERE t.status = com.abdulmunim.apik8s.model.Status.PENDING AND t.priority = com.abdulmunim.apik8s.model.Priority.URGENT")
    List<Task> findUrgentPendingTasks();

    // Custom query to find tasks by priority level (for more complex queries)
    @Query("SELECT t FROM Task t WHERE t.priority IN :priorities ORDER BY t.createdAt DESC")
    List<Task> findByPrioritiesOrderByCreatedAtDesc(List<Priority> priorities);

    // Count tasks by completion status
//    long countByCompleted(Boolean completed);

    // Count tasks by priority
    long countByPriority(Priority priority);
}