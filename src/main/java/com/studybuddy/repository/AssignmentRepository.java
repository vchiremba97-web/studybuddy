package com.studybuddy.repository;

import com.studybuddy.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByStudentId(Long studentId);

    List<Assignment> findAllByStudentIdOrderByIdAsc(Long studentId);

    Optional<Assignment> findByIdAndStudentId(Long id, Long studentId);

    long countByStudentId(Long studentId);

    @Modifying
    @Query("delete from Assignment a where a.id = :id and a.student.id = :studentId")
    int deleteByIdAndStudentId(
            @Param("id") Long id,
            @Param("studentId") Long studentId
    );
}