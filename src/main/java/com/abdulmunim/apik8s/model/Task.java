package com.abdulmunim.apik8s.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class Task {

    private  static final Logger logger = LoggerFactory.getLogger(Task.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // ADD THIS LINE
    private Long id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

//    @Column(nullable = false)
//    private Boolean completed = false;

    @Column(name= "created_at")
    @JsonProperty(access = JsonProperty.Access.READ_ONLY) // ADD THIS LINE TOO
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private Priority priority = Priority.LOW;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (priority == null) {
            priority = Priority.LOW;
        }
        logger.debug("@PrePersist: Task about to be created - Title: '{}', Priority: {}", title, priority);
    }

    @PostPersist
    protected void afterCreate() {
        logger.info("Task created with ID: {} at {}", id, createdAt);
    }

    // Keep all your existing constructors, getters, and setters exactly the same
    public Task() {
        logger.debug("Creating empty Task instance");
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.priority = Priority.LOW;
        logger.debug("Creating Task - Title: '{}', Priority: {}", title, this.priority);
    }

    public Task(String title, String description, Priority priority) {
        this.title = title;
        this.description = description;
        this.priority = priority != null ? priority : Priority.LOW;
        logger.debug("Creating Task - Title: '{}', Priority: {}", title, this.priority);
    }

    // All your existing getters and setters stay the same
    public Long getId() { return id; }
    public void setId(Long id) {
        logger.debug("Setting Task ID to: {} -> {}", this.id, id);
        this.id = id;
    }
    public String getTitle() { return title; }
    public void setTitle(String title) {
        logger.debug("Setting Task title to: '{}' -> '{}'", this.title, title);
        this.title = title;
    }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
//    public Boolean getCompleted() { return completed; }
//    public void setCompleted(Boolean completed) { this.completed = completed; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        Priority oldPriority = this.priority;
        this.priority = priority != null ? priority : Priority.LOW;
        if (!oldPriority.equals(this.priority)) {
            logger.debug("Priority changed: {} -> {}", oldPriority, this.priority);
        }
//        this.priority = priority;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }


    @Override
    public String toString() {
        return String.format("Task{id=%d, title='%s', description='%s', status=%s, createdAt=%s, priority=%s}",
                id, title, description, status, createdAt, priority);
    }
}