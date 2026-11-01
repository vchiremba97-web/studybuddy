package com.studybuddy.service;

import com.studybuddy.model.CalendarEvent;
import com.studybuddy.model.Student;
import com.studybuddy.repository.CalendarEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class CalendarEventService {

    private final CalendarEventRepository calendarEventRepository;

    public CalendarEventService(
            CalendarEventRepository calendarEventRepository) {
        this.calendarEventRepository = calendarEventRepository;
    }


    public List<CalendarEvent> getEvents(
            Student student,
            LocalDate startDate,
            LocalDate endDate) {

        if (student == null || student.getId() == null) {
            return List.of();
        }

        return calendarEventRepository
                .findAllByStudentIdAndEventDateBetween(
                        student.getId(),
                        startDate,
                        endDate
                );
    }


    public void addEvent(
            String title,
            String description,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String eventType,
            Student student) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException(
                    "A logged-in student is required.");
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter an event title.");
        }

        if (eventDate == null) {
            throw new IllegalArgumentException(
                    "Please select an event date.");
        }

        if (startTime != null && endTime != null
                && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "The end time must be after the start time.");
        }

        String cleanEventType =
                (eventType == null || eventType.isBlank())
                        ? "PERSONAL"
                        : eventType.trim().toUpperCase();

        if (!List.of(
                "ASSIGNMENT",
                "EXAM",
                "LECTURE",
                "STUDY",
                "PERSONAL"
        ).contains(cleanEventType)) {
            throw new IllegalArgumentException(
                    "Please select a valid event category.");
        }

        CalendarEvent event = new CalendarEvent();

        event.setTitle(title.trim());
        event.setDescription(
                description == null ? "" : description.trim()
        );
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setEventType(cleanEventType);
        event.setStudent(student);

        calendarEventRepository.save(event);
    }


    @Transactional
    public void updateEvent(
            Long id,
            String title,
            String description,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String eventType,
            Student student) {

        if (student == null || student.getId() == null || id == null) {
            throw new IllegalArgumentException(
                    "A valid student and event are required.");
        }

        CalendarEvent event = calendarEventRepository
                .findByIdAndStudentId(id, student.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Calendar event not found."));

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Please enter an event title.");
        }

        if (eventDate == null) {
            throw new IllegalArgumentException(
                    "Please select an event date.");
        }

        if (startTime != null && endTime != null
                && !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "The end time must be after the start time.");
        }

        String cleanEventType =
                (eventType == null || eventType.isBlank())
                        ? "PERSONAL"
                        : eventType.trim().toUpperCase();

        if (!List.of(
                "ASSIGNMENT",
                "EXAM",
                "LECTURE",
                "STUDY",
                "PERSONAL"
        ).contains(cleanEventType)) {
            throw new IllegalArgumentException(
                    "Please select a valid event category.");
        }

        event.setTitle(title.trim());
        event.setDescription(
                description == null ? "" : description.trim()
        );
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setEventType(cleanEventType);

        calendarEventRepository.save(event);
    }


    @Transactional
    public void deleteEvent(Long id, Student student) {

        if (student == null || student.getId() == null || id == null) {
            return;
        }

        calendarEventRepository
                .findByIdAndStudentId(id, student.getId())
                .ifPresent(calendarEventRepository::delete);
    }
}
