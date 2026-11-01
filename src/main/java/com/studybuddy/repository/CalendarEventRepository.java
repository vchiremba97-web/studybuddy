package com.studybuddy.repository;

import com.studybuddy.model.CalendarEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CalendarEventRepository
        extends JpaRepository<CalendarEvent, Long> {

    List<CalendarEvent> findAllByStudentIdAndEventDateBetween(
            Long studentId,
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<CalendarEvent> findByIdAndStudentId(
            Long id,
            Long studentId
    );
}
