package com.abdulmunim.apik8s.controller;

import com.abdulmunim.apik8s.model.Priority;
import com.abdulmunim.apik8s.model.Task;
import com.abdulmunim.apik8s.repository.TaskRepository;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*")
@Tag(name="Task Management", description = "API for managing tasks with priorities")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskRepository taskRepository;


    @Operation(
            summary = "Create a new task",
            description = "Creates a new task with title, description, completion status and priority level"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Task created successfully",
            content = @Content(schema = @Schema(implementation = Task.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })

    // UPDATED: Create a new Task with debugging
    @PostMapping
    public ResponseEntity<Task> createTask(

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Task object to created",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Task.class))
            )
            @Valid @RequestBody Task task) {
        logger.info("POST /api/tasks - Creating new task");
        logger.debug("Request body - Title: '{}', Description: '{}', Priority: {}, Completed: {}",
                task.getTitle(), task.getDescription(), task.getPriority(), task.getCompleted());
        try {
            // Ensure it's treated as a new entity (ID should be null for creation)
            if (task.getId() != null) {
                logger.warn("Received task with non-null ID: {}, setting to null for creation", task.getId());
                task.setId(null);
            }

            // Set default values if needed
            if (task.getPriority() == null) {
                logger.debug("Priority is null, setting default to LOW");
                task.setPriority(Priority.LOW);
            }
            if (task.getCompleted() == null) {
                logger.debug("Completed is null, setting default to false");
                task.setCompleted(false);
            }

            // Force it to be treated as new entity
//            task.setId(null);
            logger.debug("Saving task to database...");

            Task savedTask = taskRepository.save(task);

            logger.info("Task created successfully - ID: {}, Title: '{}', Priority: {}",
                    savedTask.getId(), savedTask.getTitle(), savedTask.getPriority());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTask);

        } catch (Exception e) {
            logger.error("Error creating task: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Get all tasks", description = "Retrieve all tasks from the database")
    @ApiResponse(responseCode = "200",description = "Successfully retrived all tasks")
    // Keep all your existing methods exactly the same
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        logger.info("GET /api/tasks - Retrieving all tasks");
        try {
            List<Task> tasks = taskRepository.findAll();
            logger.info("Retrieved {} tasks", tasks.size());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving all tasks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get task by ID", description = "Retrieves a specific task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task found"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(
            @Parameter(description = "ID of the task to retrieve", required = true)
            @PathVariable(value = "id") Long taskId) {

        logger.info("GET /api/tasks/{} - Retrieving task by ID", taskId);
        try {
            return taskRepository.findById(taskId)
                    .map(task -> {
                        logger.info("Task found - ID: {}, Title: '{}'", task.getId(), task.getTitle());
                        return ResponseEntity.ok(task);
                    })
                    .orElseGet(() -> {
                        logger.warn("Task not found with ID: {}", taskId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error retrieving task with ID {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Update a task", description = "Updates an existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(
            @Parameter(description = "ID of the task to update", required = true)
            @PathVariable(value = "id") Long taskId,
            @Valid @RequestBody Task taskDetails) {
        logger.info("PUT /api/tasks/{} - Updating task", taskId);
        logger.debug("Update data - Title: '{}', Priority: {}, Completed: {}",
                taskDetails.getTitle(), taskDetails.getPriority(), taskDetails.getCompleted());

        try {
            return taskRepository.findById(taskId)
                    .map(task -> {
                        logger.debug("Found existing task - Title: '{}'", task.getTitle());

                        task.setTitle(taskDetails.getTitle());
                        task.setDescription(taskDetails.getDescription());
                        task.setCompleted(taskDetails.getCompleted());
                        task.setPriority(taskDetails.getPriority());

                        Task updatedTask = taskRepository.save(task);
                        logger.info("Task updated successfully - ID: {}, Title: '{}'",
                                updatedTask.getId(), updatedTask.getTitle());

                        return ResponseEntity.ok(updatedTask);
                    })
                    .orElseGet(() -> {
                        logger.warn("Task not found for update with ID: {}", taskId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error updating task with ID {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Delete a task", description = "Deletes a task by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Task deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Task not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(
            @Parameter(description = "ID of the task to delete", required = true)
            @PathVariable(value = "id") Long taskId) {
        logger.info("DELETE /api/tasks/{} - Deleting task", taskId);
        try {
            return taskRepository.findById(taskId)
                    .map(task -> {
                        logger.debug("Found task to delete - Title: '{}'", task.getTitle());
                        taskRepository.delete(task);
                        logger.info("Task deleted successfully - ID: {}", taskId);
                        return ResponseEntity.noContent().<Void>build();
                    })
                    .orElseGet(() -> {
                        logger.warn("Task not found for deletion with ID: {}", taskId);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error deleting task with ID {}: {}", taskId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Get tasks by priority
    @Operation(summary = "Get tasks by priority", description = "Retrieves all tasks with a specific priority level")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<Task>> getTasksByPriority(
            @Parameter(description = "Priority level to filter by", required = true)
            @PathVariable Priority priority) {
        logger.info("GET /api/tasks/priority/{} - Retrieving tasks by priority", priority);
        try {
            List<Task> tasks = taskRepository.findByPriority(priority);
            logger.info("Found {} tasks with priority: {}", tasks.size(), priority);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving tasks by priority {}: {}", priority, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Get tasks ordered by priority
    @Operation(summary = "Get tasks sorted by priority", description = "Retrieves all tasks ordered by priority (highest first)")
    @GetMapping("/sorted")
    public ResponseEntity<List<Task>> getTasksSortedByPriority() {
        logger.info("GET /api/tasks/sorted - Retrieving tasks sorted by priority");
        try {
            List<Task> tasks = taskRepository.findByOrderByPriorityDesc();
            logger.info("Retrieved {} tasks sorted by priority", tasks.size());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving sorted tasks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get completed tasks", description = "Retrieves all completed tasks")
    @GetMapping("/completed")
    public ResponseEntity<List<Task>> getCompletedTasks() {
        logger.info("GET /api/tasks/completed - Retrieving completed tasks");
        try {
            List<Task> tasks = taskRepository.findByCompleted(true);
            logger.info("Found {} completed tasks", tasks.size());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving completed tasks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Get pending tasks", description = "Retrieves all pending (not completed) tasks")
    @GetMapping("/pending")
    public ResponseEntity<List<Task>> getPendingTasks() {
        logger.info("GET /api/tasks/pending - Retrieving pending tasks");
        try {
            List<Task> tasks = taskRepository.findByCompleted(false);
            logger.info("Found {} pending tasks", tasks.size());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Error retrieving pending tasks: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}