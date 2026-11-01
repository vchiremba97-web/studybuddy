package com.studybuddy.repository;

import com.studybuddy.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findAllByStudentId(Long studentId);

    List<Course> findAllByStudentIdOrderByIdAsc(Long studentId);

    Optional<Course> findByIdAndStudentId(Long id, Long studentId);

    long countByStudentId(Long studentId);

    @Modifying
    @Query("delete from Course c where c.id = :id and c.student.id = :studentId")
    int deleteByIdAndStudentId(
            @Param("id") Long id,
            @Param("studentId") Long studentId
    );
}