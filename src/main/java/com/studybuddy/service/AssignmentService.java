
package com.studybuddy.service;

import com.studybuddy.model.Assignment;
import com.studybuddy.model.Student;
import com.studybuddy.repository.AssignmentRepository;
import com.studybuddy.repository.StudentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;

    public AssignmentService(
            AssignmentRepository assignmentRepository,
            StudentRepository studentRepository) {

        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
    }

    public List<Assignment> getAll(Long studentId) {

        if (studentId == null) {
            return List.of();
        }

        return assignmentRepository.findAllByStudentIdOrderByIdAsc(studentId);
    }

    public List<Assignment> getAll(Student student) {

        if (student == null || student.getId() == null) {
            return List.of();
        }

        return getAll(student.getId());
    }

    public long count(Long studentId) {

        return studentId == null
                ? 0
                : assignmentRepository.countByStudentId(studentId);
    }

    @Transactional
    public void add(
            String title,
            String course,
            String dueDate,
            String priority,
            Long studentId) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "A logged-in student is required."
            );
        }

        if (title == null || title.isBlank()
                || course == null || course.isBlank()
                || dueDate == null || dueDate.isBlank()) {

            throw new IllegalArgumentException(
                    "Please enter the assignment title, course and due date."
            );
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
        assignment.setCompleted(false);
        assignment.setStudent(
                studentRepository.getReferenceById(studentId)
        );

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void add(
            String title,
            String course,
            String dueDate,
            String priority,
            Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException(
                    "A logged-in student is required."
            );
        }

        if (title == null || title.isBlank()
                || course == null || course.isBlank()
                || dueDate == null || dueDate.isBlank()) {

            throw new IllegalArgumentException(
                    "Please enter the assignment title, course and due date."
            );
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
        assignment.setCompleted(false);
        assignment.setStudent(student);

        assignmentRepository.save(assignment);
    }

    @Transactional
    public void updateCompletion(
            Long id,
            boolean completed,
            Student student) {

        if (id == null || student == null || student.getId() == null) {
            throw new IllegalArgumentException(
                    "A valid assignment and logged-in student are required."
            );
        }

        Optional<Assignment> assignment =
                assignmentRepository.findByIdAndStudentId(
                        id,
                        student.getId()
                );

        if (assignment.isEmpty()) {
            throw new IllegalArgumentException(
                    "Assignment not found."
            );
        }

        Assignment existingAssignment = assignment.get();
        existingAssignment.setCompleted(completed);

        assignmentRepository.save(existingAssignment);
    }

    @Transactional
    public void delete(Long id, Long studentId) {

        if (id == null || studentId == null) {
            return;
        }

        assignmentRepository.deleteByIdAndStudentId(id, studentId);
    }

    @Transactional
    public void delete(Long id, Student student) {

        if (id == null || student == null || student.getId() == null) {
            return;
        }

        assignmentRepository.deleteByIdAndStudentId(
                id,
                student.getId()
        );
    }
}
