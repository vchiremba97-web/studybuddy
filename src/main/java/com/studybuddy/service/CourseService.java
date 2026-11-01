
package com.studybuddy.service;

import com.studybuddy.model.Course;
import com.studybuddy.model.Student;
import com.studybuddy.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses(Student student) {
        if (student == null || student.getId() == null) {
            return List.of();
        }

        return courseRepository.findAllByStudentId(student.getId());
    }

    public void addCourse(
            String name,
            String code,
            String instructor,
            String description,
            Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException("A logged-in student is required.");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Please enter a course name.");
        }

        Course course = new Course();

        course.setName(name.trim());
        course.setCode(code == null ? "" : code.trim());
        course.setInstructor(instructor == null ? "" : instructor.trim());
        course.setDescription(description == null ? "" : description.trim());
        course.setStudent(student);

        courseRepository.save(course);
    }

    @Transactional
    public void deleteCourse(Long id, Student student) {
        if (id == null || student == null || student.getId() == null) {
            return;
        }

        courseRepository.findByIdAndStudentId(id, student.getId())
                .ifPresent(courseRepository::delete);
    }
}
