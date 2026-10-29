
package com.studybuddy.model;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class StudyTask {

    private long id;

    @NotBlank(message = "Please enter a task title.")
    private String title;

    @NotBlank(message = "Please enter a subject.")
    private String subject;

    @NotNull(message = "Please choose a due date.")
    @FutureOrPresent(message = "Due date cannot be in the past.")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @NotBlank(message = "Please choose a priority.")
    private String priority = "Medium";

    private boolean completed;

    public StudyTask() {
    }

    public StudyTask(long id, String title, String subject,
                     LocalDate dueDate, String priority,
                     boolean completed) {
        this.id = id;
        this.title = title;
        this.subject = subject;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = completed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
