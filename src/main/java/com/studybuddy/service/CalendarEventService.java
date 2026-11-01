package com.studybuddy.service;

import com.studybuddy.model.CalendarEvent;
import com.studybuddy.model.Student;
import com.studybuddy.repository.CalendarEventRepository;
import com.studybuddy.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class CalendarEventService {

    private static final Set<String> EVENT_TYPES =
            Set.of("ASSIGNMENT", "EXAM", "LECTURE", "STUDY", "PERSONAL");

    private final CalendarEventRepository calendarEventRepository;
    private final StudentRepository studentRepository;

    public CalendarEventService(
            CalendarEventRepository calendarEventRepository,
            StudentRepository studentRepository) {

        this.calendarEventRepository = calendarEventRepository;
        this.studentRepository = studentRepository;
    }

    public List<CalendarEvent> getEvents(
            Long studentId,
            LocalDate startDate,
            LocalDate endDate) {

        if (studentId == null || startDate == null || endDate == null
                || endDate.isBefore(startDate)) {
            return List.of();
        }

        return calendarEventRepository.findInRange(
                studentId, startDate, endDate);
    }


    public List<CalendarEvent> getEvents(
            Student student,
            LocalDate startDate,
            LocalDate endDate) {

        if (student == null || student.getId() == null) {
            return List.of();
        }

        return getEvents(student.getId(), startDate, endDate);
    }


    @Transactional
    public void addEvent(
            String title,
            String description,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String eventType,
            Long studentId) {

        if (studentId == null) {
            throw new IllegalArgumentException(
                    "A logged-in student is required.");
        }

        CalendarEvent event = new CalendarEvent();

        applyDetails(event, title, description, eventDate,
                startTime, endTime, eventType);

        event.setStudent(studentRepository.getReferenceById(studentId));

        calendarEventRepository.save(event);
    }


    @Transactional
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

        CalendarEvent event = new CalendarEvent();

        applyDetails(event, title, description, eventDate,
                startTime, endTime, eventType);

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
            Long studentId) {

        if (studentId == null || id == null) {
            throw new IllegalArgumentException(
                    "A valid student and event are required.");
        }

        CalendarEvent event = calendarEventRepository
                .findByIdAndStudentId(id, studentId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Calendar event not found."));

        applyDetails(event, title, description, eventDate,
                startTime, endTime, eventType);
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

        updateEvent(id, title, description, eventDate,
                startTime, endTime, eventType, student.getId());
    }


    @Transactional
    public void deleteEvent(Long id, Long studentId) {

        if (id == null || studentId == null) {
            return;
        }

        calendarEventRepository.deleteByIdAndStudentId(id, studentId);
    }


    @Transactional
    public void deleteEvent(Long id, Student student) {

        if (student == null || student.getId() == null || id == null) {
            return;
        }

        deleteEvent(id, student.getId());
    }


    private static void applyDetails(
            CalendarEvent event,
            String title,
            String description,
            LocalDate eventDate,
            LocalTime startTime,
            LocalTime endTime,
            String eventType) {

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
                        : eventType.trim().toUpperCase(Locale.ROOT);

        if (!EVENT_TYPES.contains(cleanEventType)) {
            throw new IllegalArgumentException(
                    "Please select a valid event category.");
        }

        event.setTitle(title.trim());
        event.setDescription(
                description == null ? "" : description.trim());
        event.setEventDate(eventDate);
        event.setStartTime(startTime);
        event.setEndTime(endTime);
        event.setEventType(cleanEventType);
    }
}