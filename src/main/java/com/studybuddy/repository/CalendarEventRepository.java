package com.studybuddy.repository;

import com.studybuddy.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository
        extends JpaRepository<CalendarEvent, Long> {

    @Query("""
            select e from CalendarEvent e
            where e.student.id = :studentId
              and e.eventDate between :startDate and :endDate
            order by e.eventDate, e.startTime
            """)
    List<CalendarEvent> findInRange(
            @Param("studentId") Long studentId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    List<CalendarEvent> findAllByStudentIdAndEventDateBetween(
            Long studentId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<CalendarEvent> findByIdAndStudentId(
            Long id,
            Long studentId
    );

    @Modifying
    @Query("delete from CalendarEvent e where e.id = :id and e.student.id = :studentId")
    int deleteByIdAndStudentId(
            @Param("id") Long id,
            @Param("studentId") Long studentId
    );
}