package com.abdulmunim.apik8s.controller;

import com.abdulmunim.apik8s.model.Priority;
import com.abdulmunim.apik8s.model.Status;
import com.abdulmunim.apik8s.model.Task;
import com.abdulmunim.apik8s.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task testTask;

    @BeforeEach
    void setUp() {
        testTask = new Task();
        testTask.setId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        // ✅ Use status instead of completed
        testTask.setStatus(Status.PENDING);
        testTask.setPriority(Priority.HIGH);
    }

    @Test
    void createTask_ShouldReturnCreatedTask() throws Exception {
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("PENDING")); // ✅ Verify status

        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void getAllTasks_ShouldReturnListOfTasks() throws Exception {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(testTask));

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Task"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));

        verify(taskRepository).findAll();
    }

    @Test
    void getTaskById_ShouldReturnTask() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Task"))
                .andExpect(jsonPath("$.status").value("PENDING"));

        verify(taskRepository).findById(1L);
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() throws Exception {
        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Task");
        updatedTask.setPriority(Priority.MEDIUM);
        updatedTask.setStatus(Status.IN_PROGRESS); // ✅ Include status in update

        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedTask)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldReturnNoContent() throws Exception {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(testTask));

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());

        verify(taskRepository).findById(1L);
        verify(taskRepository).delete(testTask);
    }
}