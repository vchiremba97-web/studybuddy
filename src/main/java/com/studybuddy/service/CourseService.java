
package com.studybuddy.service;

import com.studybuddy.model.Course;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourseService {

    private final List<Course> courses = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public synchronized List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public synchronized void addCourse(
            String name,
            String code,
            String instructor,
            String description) {

        if (name == null || name.isBlank()) {
            return;
        }

        Course course = new Course(
                nextId.getAndIncrement(),
                name.trim(),
                code == null ? "" : code.trim(),
                instructor == null ? "" : instructor.trim(),
                description == null ? "" : description.trim()
        );

        courses.add(course);
    }

    public synchronized void deleteCourse(Long id) {
        if (id == null) {
            return;
        }

        courses.removeIf(course -> id.equals(course.getId()));
    }
}
