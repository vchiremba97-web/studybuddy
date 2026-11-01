package com.studybuddy.service;

import com.studybuddy.model.Course;
import com.studybuddy.model.Student;
import com.studybuddy.repository.CourseRepository;
import com.studybuddy.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;

    public CourseService(CourseRepository courseRepository,
                         StudentRepository studentRepository) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
    }

    public List<Course> getAllCourses(Long studentId) {
        if (studentId == null) {
            return List.of();
        }

        return courseRepository.findAllByStudentIdOrderByIdAsc(studentId);
    }

    public List<Course> getAllCourses(Student student) {
        if (student == null || student.getId() == null) {
            return List.of();
        }

        return getAllCourses(student.getId());
    }

    public long countCourses(Long studentId) {
        return studentId == null ? 0 : courseRepository.countByStudentId(studentId);
    }

    @Transactional
    public void addCourse(String name, String code, String instructor,
                          String description, Long studentId) {

        if (studentId == null) {
            throw new IllegalArgumentException("A logged-in student is required.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Please enter a course name.");
        }

        Course course = new Course();
        course.setName(name.trim());
        course.setCode(trimToEmpty(code));
        course.setInstructor(trimToEmpty(instructor));
        course.setDescription(trimToEmpty(description));
        course.setStudent(studentRepository.getReferenceById(studentId));

        courseRepository.save(course);
    }

    @Transactional
    public void addCourse(String name, String code, String instructor,
                          String description, Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException("A logged-in student is required.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Please enter a course name.");
        }

        Course course = new Course();
        course.setName(name.trim());
        course.setCode(trimToEmpty(code));
        course.setInstructor(trimToEmpty(instructor));
        course.setDescription(trimToEmpty(description));
        course.setStudent(student);

        courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id, Long studentId) {
        if (id == null || studentId == null) {
            return;
        }

        courseRepository.deleteByIdAndStudentId(id, studentId);
    }

    @Transactional
    public void deleteCourse(Long id, Student student) {
        if (id == null || student == null || student.getId() == null) {
            return;
        }

        courseRepository.deleteByIdAndStudentId(id, student.getId());
    }

    private static String trimToEmpty(String value) {
        return value == null ? "" : value.trim();
    }
}