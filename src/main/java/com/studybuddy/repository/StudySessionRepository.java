
package com.studybuddy.repository;

import com.studybuddy.model.StudySession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudySessionRepository
        extends JpaRepository<StudySession, Long> {

    List<StudySession> findAllByStudentIdOrderByStartTimeDesc(
            Long studentId
    );

    @Query("""
        SELECT COALESCE(SUM(s.durationSeconds), 0)
        FROM StudySession s
        WHERE s.student.id = :studentId
        """)
    Long getTotalStudySeconds(
            @Param("studentId") Long studentId
    );

    @Query("""
        SELECT COALESCE(SUM(s.durationSeconds), 0)
        FROM StudySession s
        WHERE s.student.id = :studentId
        AND s.course.id = :courseId
        """)
    Long getTotalStudySecondsByCourse(
            @Param("studentId") Long studentId,
            @Param("courseId") Long courseId
    );
    long countByStudentId(Long studentId);
}
