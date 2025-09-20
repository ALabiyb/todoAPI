package com.abdulmunim.apik8s.repository;

import com.abdulmunim.apik8s.model.Priority;
import com.abdulmunim.apik8s.model.Task;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

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

    @Test
    public void findByCompleted_ShouldReturnCompletedTasks() {
        // Given
        Task completedTask = new Task("Completed Task", "Description");
        completedTask.setCompleted(true);

        Task pendingTask = new Task("Pending Task", "Description");
        pendingTask.setCompleted(false);

        entityManager.persist(completedTask);
        entityManager.persist(pendingTask);
        entityManager.flush();

        // When
        List<Task> completedTasks = taskRepository.findByCompleted(true);

        // Then
        assertThat(completedTasks).hasSize(1);
        assertThat(completedTasks.get(0).getTitle()).isEqualTo("Completed Task");
    }
}