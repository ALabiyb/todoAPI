package com.abdulmunim.apik8s.repository;

import com.abdulmunim.apik8s.model.Priority;
import com.abdulmunim.apik8s.model.Status;
import com.abdulmunim.apik8s.model.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import com.abdulmunim.apik8s.model.Task;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class TaskRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void findByPriority_ShouldReturnTasksWithSpecificPriority() {
        // Given
        Task highPriorityTask = new Task("High Priority Task", "Description");
        highPriorityTask.setPriority(Priority.HIGH);

        Task mediumPriorityTask = new Task("Medium Priority Task", "Description");
        mediumPriorityTask.setPriority(Priority.MEDIUM);

        entityManager.persist(highPriorityTask);
        entityManager.persist(mediumPriorityTask);
        entityManager.flush();

        // When
        List<Task> highPriorityTasks = taskRepository.findByPriority(Priority.HIGH);

        // Then
        assertThat(highPriorityTasks).hasSize(1);
        assertThat(highPriorityTasks.get(0).getTitle()).isEqualTo("High Priority Task");
    }

    // ✅ NEW: Test findByStatus
    @Test
    public void findByStatus_ShouldReturnTasksWithSpecificStatus() {
        // Given
        Task pendingTask = new Task("Pending Task", "Description");
        pendingTask.setStatus(Status.PENDING);

        Task completedTask = new Task("Completed Task", "Description");
        completedTask.setStatus(Status.COMPLETED);

        entityManager.persist(pendingTask);
        entityManager.persist(completedTask);
        entityManager.flush();

        // When
        List<Task> pendingTasks = taskRepository.findByStatus(Status.PENDING);

        // Then
        assertThat(pendingTasks).hasSize(1);
        assertThat(pendingTasks.get(0).getTitle()).isEqualTo("Pending Task");
    }

    // ✅ Optional: Test COMPLETED status
    @Test
    public void findByStatus_Completed_ShouldReturnCompletedTasks() {
        Task task1 = new Task("Task 1", "Desc");
        task1.setStatus(Status.COMPLETED);

        Task task2 = new Task("Task 2", "Desc");
        task2.setStatus(Status.PENDING);

        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.flush();

        List<Task> completed = taskRepository.findByStatus(Status.COMPLETED);

        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getStatus()).isEqualTo(Status.COMPLETED);
    }
}