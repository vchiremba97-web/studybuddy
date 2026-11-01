
package com.studybuddy.repository;

import com.studybuddy.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByStudentId(Long studentId);

    Optional<Course> findByIdAndStudentId(Long id, Long studentId);
}
