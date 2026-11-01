
package com.studybuddy.service;

import com.studybuddy.model.Course;
import com.studybuddy.model.Student;
import com.studybuddy.model.StudySession;
import com.studybuddy.repository.CourseRepository;
import com.studybuddy.repository.StudySessionRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final CourseRepository courseRepository;

    public StudySessionService(
            StudySessionRepository studySessionRepository,
            CourseRepository courseRepository) {

        this.studySessionRepository = studySessionRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void saveSession(
            Long courseId,
            Student student,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long durationSeconds) {

        if (student == null || student.getId() == null) {
            throw new IllegalArgumentException(
                    "A logged-in student is required."
            );
        }

        if (startTime == null || endTime == null
                || !endTime.isAfter(startTime)) {
            throw new IllegalArgumentException(
                    "Invalid study session times."
            );
        }

        if (durationSeconds == null || durationSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Study duration must be greater than zero."
            );
        }

        long elapsedSeconds =
                Duration.between(startTime, endTime).getSeconds();

        if (durationSeconds > elapsedSeconds) {
            throw new IllegalArgumentException(
                    "Study duration cannot exceed the session time."
            );
        }

        Course course = courseRepository
                .findByIdAndStudentId(courseId, student.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Course not found for this student."
                ));

        StudySession session = new StudySession();
        session.setStudent(student);
        session.setCourse(course);
        session.setStartTime(startTime);
        session.setEndTime(endTime);
        session.setDurationSeconds(durationSeconds);

        studySessionRepository.save(session);
    }

    public List<StudySession> getSessions(Student student) {
        if (student == null || student.getId() == null) {
            return List.of();
        }

        return studySessionRepository
                .findAllByStudentIdOrderByStartTimeDesc(
                        student.getId()
                );
    }

    public long getTotalStudySeconds(Student student) {
        if (student == null || student.getId() == null) {
            return 0;
        }

        Long total = studySessionRepository
                .getTotalStudySeconds(student.getId());

        return total == null ? 0 : total;
    }

    public long getTotalSessions(Student student) {
        if (student == null || student.getId() == null) {
            return 0;
        }

        return studySessionRepository
                .countByStudentId(student.getId());
    }
}
