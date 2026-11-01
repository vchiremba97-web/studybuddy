
package com.studybuddy.controller;

import com.studybuddy.model.Student;
import com.studybuddy.model.Assignment;
import com.studybuddy.model.CalendarEvent;
import com.studybuddy.model.StudySession;

import com.studybuddy.service.AssignmentService;
import com.studybuddy.service.CalendarEventService;
import com.studybuddy.service.CourseService;
import com.studybuddy.service.StudentService;
import com.studybuddy.service.StudySessionService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class PageController {

    private static final DateTimeFormatter MONTH_TITLE =
            DateTimeFormatter.ofPattern("MMMM yyyy");

    private static final DateTimeFormatter DAY_LABEL =
            DateTimeFormatter.ofPattern("EEE d");

    private final CourseService courseService;
    private final AssignmentService assignmentService;
    private final StudentService studentService;
    private final CalendarEventService calendarEventService;
    private final StudySessionService studySessionService;

    public PageController(
            CourseService courseService,
            AssignmentService assignmentService,
            StudentService studentService,
            CalendarEventService calendarEventService,
            StudySessionService studySessionService) {

        this.courseService = courseService;
        this.assignmentService = assignmentService;
        this.studentService = studentService;
        this.calendarEventService = calendarEventService;
        this.studySessionService = studySessionService;
    }

    private Student getCurrentStudent(UserDetails userDetails) {

        if (userDetails == null) {
            throw new IllegalStateException("No student is logged in.");
        }

        Student student = studentService.findByEmail(
                userDetails.getUsername()
        );

        if (student == null) {
            throw new IllegalStateException("Student account not found.");
        }

        return student;
    }

    private String formatStudyDuration(long seconds) {

        long totalMinutes = Math.round(seconds / 60.0);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;

        if (hours > 0 && minutes > 0) {
            return hours + " hr " + minutes + " min";
        }

        if (hours > 0) {
            return hours + (hours == 1 ? " hr" : " hrs");
        }

        return totalMinutes + " min";
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        model.addAttribute("student", student);
        model.addAttribute("studentName", student.getFullName());
        model.addAttribute("educationLevel", student.getEducationLevel());
        model.addAttribute("institutionName", student.getInstitutionName());

        model.addAttribute(
                "courses",
                courseService.getAllCourses(student)
        );

        model.addAttribute(
                "assignments",
                assignmentService.getAll(student)
        );

        model.addAttribute(
                "courseCount",
                courseService.countCourses(student.getId())
        );

        model.addAttribute(
                "assignmentCount",
                assignmentService.count(student.getId())
        );

        return "dashboard";
    }

    @GetMapping("/courses")
    public String courses(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        model.addAttribute(
                "courses",
                courseService.getAllCourses(student)
        );

        model.addAttribute("studentName", student.getFullName());

        return "courses";
    }

    @GetMapping("/assignments")
    public String assignments(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        model.addAttribute(
                "assignments",
                assignmentService.getAll(student)
        );

        model.addAttribute(
                "courses",
                courseService.getAllCourses(student)
        );

        model.addAttribute("studentName", student.getFullName());

        return "assignments";
    }

    @GetMapping("/progress")
    public String progress(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        List<Assignment> assignments =
                assignmentService.getAll(student);

        long assignmentCount = assignments.size();

        long completedCount = assignments.stream()
                .filter(Assignment::isCompleted)
                .count();

        long pendingCount = assignmentCount - completedCount;

        int completionPercentage = assignmentCount == 0
                ? 0
                : (int) Math.round(
                completedCount * 100.0 / assignmentCount
        );

        List<StudySession> sessions =
                studySessionService.getSessions(student);

        long totalStudySeconds =
                studySessionService.getTotalStudySeconds(student);

        long totalSessions =
                studySessionService.getTotalSessions(student);

        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.minusDays(6);

        Map<LocalDate, Long> dailyStudySeconds = new HashMap<>();

        for (StudySession session : sessions) {

            LocalDate sessionDate =
                    session.getStartTime().toLocalDate();

            if (!sessionDate.isBefore(weekStart)
                    && !sessionDate.isAfter(today)) {

                dailyStudySeconds.merge(
                        sessionDate,
                        session.getDurationSeconds(),
                        Long::sum
                );
            }
        }

        long maximumDailySeconds = 0;

        for (long seconds : dailyStudySeconds.values()) {
            maximumDailySeconds = Math.max(
                    maximumDailySeconds,
                    seconds
            );
        }

        List<Map<String, Object>> weeklyStudyActivity =
                new ArrayList<>();

        for (int i = 0; i < 7; i++) {

            LocalDate date = weekStart.plusDays(i);

            long seconds = dailyStudySeconds.getOrDefault(
                    date,
                    0L
            );

            int percentage = maximumDailySeconds == 0
                    ? 0
                    : (int) Math.round(
                    seconds * 100.0 / maximumDailySeconds
            );

            Map<String, Object> day = new HashMap<>();

            day.put("dayLabel", date.format(DAY_LABEL));
            day.put("durationLabel", formatStudyDuration(seconds));
            day.put("percentage", percentage);

            weeklyStudyActivity.add(day);
        }

        model.addAttribute(
                "courseCount",
                courseService.countCourses(student.getId())
        );

        model.addAttribute("assignmentCount", assignmentCount);
        model.addAttribute("completedCount", completedCount);
        model.addAttribute("pendingCount", pendingCount);
        model.addAttribute("completionPercentage", completionPercentage);

        model.addAttribute(
                "totalStudyTime",
                formatStudyDuration(totalStudySeconds)
        );

        model.addAttribute("totalSessions", totalSessions);
        model.addAttribute("weeklyStudyActivity", weeklyStudyActivity);

        model.addAttribute("studentName", student.getFullName());

        return "progress";
    }

    @GetMapping("/calendar")
    public String calendar(
            @RequestParam(required = false) String month,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        YearMonth selectedMonth;

        try {
            selectedMonth = (month == null || month.isBlank())
                    ? YearMonth.now()
                    : YearMonth.parse(month);
        } catch (Exception exception) {
            selectedMonth = YearMonth.now();
        }

        LocalDate firstDay = selectedMonth.atDay(1);
        LocalDate lastDay = selectedMonth.atEndOfMonth();

        LocalDate calendarStart = firstDay.minusDays(
                firstDay.getDayOfWeek().getValue() - 1
        );

        LocalDate calendarEnd = lastDay.plusDays(
                7 - lastDay.getDayOfWeek().getValue()
        );

        List<LocalDate> calendarDays = new ArrayList<>();

        for (LocalDate date = calendarStart;
             !date.isAfter(calendarEnd);
             date = date.plusDays(1)) {

            calendarDays.add(date);
        }

        List<CalendarEvent> events =
                calendarEventService.getEvents(
                        student,
                        calendarStart,
                        calendarEnd
                );

        Map<LocalDate, List<CalendarEvent>> eventsByDate =
                new HashMap<>();

        for (CalendarEvent event : events) {
            eventsByDate
                    .computeIfAbsent(
                            event.getEventDate(),
                            key -> new ArrayList<>()
                    )
                    .add(event);
        }

        model.addAttribute("studentName", student.getFullName());
        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("eventsByDate", eventsByDate);

        model.addAttribute(
                "selectedMonth",
                selectedMonth.toString()
        );

        model.addAttribute(
                "selectedMonthValue",
                selectedMonth.getMonthValue()
        );

        model.addAttribute(
                "monthTitle",
                selectedMonth.format(MONTH_TITLE)
        );

        model.addAttribute(
                "previousMonth",
                selectedMonth.minusMonths(1).toString()
        );

        model.addAttribute(
                "nextMonth",
                selectedMonth.plusMonths(1).toString()
        );

        model.addAttribute("today", LocalDate.now());

        return "calendar";
    }

    @PostMapping("/calendar/events/add")
    public String addCalendarEvent(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam LocalDate eventDate,
            @RequestParam(required = false) LocalTime startTime,
            @RequestParam(required = false) LocalTime endTime,
            @RequestParam String eventType,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        calendarEventService.addEvent(
                title,
                description,
                eventDate,
                startTime,
                endTime,
                eventType,
                student
        );

        return "redirect:/calendar?month=" +
                YearMonth.from(eventDate);
    }

    @PostMapping("/courses/add")
    public String addCourse(
            @RequestParam String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String description,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        courseService.addCourse(
                name,
                code,
                instructor,
                description,
                student
        );

        return "redirect:/courses";
    }

    @PostMapping("/courses/delete")
    public String deleteCourse(
            @RequestParam Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        courseService.deleteCourse(id, student);

        return "redirect:/courses";
    }

    @PostMapping("/assignments/add")
    public String addAssignment(
            @RequestParam String title,
            @RequestParam String course,
            @RequestParam String dueDate,
            @RequestParam(required = false) String priority,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        assignmentService.add(
                title,
                course,
                dueDate,
                priority,
                student
        );

        return "redirect:/assignments";
    }

    @PostMapping("/assignments/toggle")
    public String toggleAssignmentCompletion(
            @RequestParam Long id,
            @RequestParam boolean completed,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        assignmentService.updateCompletion(
                id,
                completed,
                student
        );

        return "redirect:/assignments";
    }
}
