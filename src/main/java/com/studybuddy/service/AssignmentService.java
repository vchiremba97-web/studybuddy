
package com.studybuddy.service;

import com.studybuddy.model.Assignment;
import com.studybuddy.model.Student;
import com.studybuddy.repository.AssignmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;

    public AssignmentService(AssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    public List<Assignment> getAll(Student student) {
        if (student == null || student.getId() == null) {
            return List.of();
        }

        return assignmentRepository.findAllByStudentId(student.getId());
    }

    public void add(
            String title,
            String course,
            String dueDate,
            String priority,
            Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException("A logged-in student is required.");
        }

        if (title == null || title.isBlank()
                || course == null || course.isBlank()
                || dueDate == null || dueDate.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter the assignment title, course and due date.");
        }

        String cleanPriority =
                (priority == null || priority.isBlank())
                        ? "Medium"
                        : priority.trim();

        Assignment assignment = new Assignment();

        assignment.setTitle(title.trim());
        assignment.setCourse(course.trim());
        assignment.setDueDate(dueDate.trim());
        assignment.setPriority(cleanPriority);
        assignment.setStudent(student);

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void delete(Long id, Student student) {
        if (id == null || student == null || student.getId() == null) {
            return;
        }

        assignmentRepository.findByIdAndStudentId(id, student.getId())
                .ifPresent(assignmentRepository::delete);
    }
}
