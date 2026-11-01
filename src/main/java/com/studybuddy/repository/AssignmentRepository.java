package com.studybuddy.repository;

import com.studybuddy.model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findAllByStudentId(Long studentId);

    Optional<Assignment> findByIdAndStudentId(Long id, Long studentId);
}
