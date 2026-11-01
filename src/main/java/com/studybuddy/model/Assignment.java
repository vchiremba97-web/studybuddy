
package com.studybuddy.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "assignments",
        indexes = @Index(name = "idx_assignments_student", columnList = "student_id")
)
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String course;

    private String dueDate;

    private String priority;

    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    public Assignment() {
    }

    public Assignment(Long id, String title, String course,
                      String dueDate, String priority) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.dueDate = dueDate;
        this.priority = priority;
        this.completed = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
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

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }
}
