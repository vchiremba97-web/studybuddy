
package com.studybuddy.controller;

import com.studybuddy.model.Student;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
public class StudyTimerController {

    private final CourseService courseService;
    private final StudentService studentService;
    private final StudySessionService studySessionService;

    public StudyTimerController(
            CourseService courseService,
            StudentService studentService,
            StudySessionService studySessionService) {

        this.courseService = courseService;
        this.studentService = studentService;
        this.studySessionService = studySessionService;
    }

    private Student getCurrentStudent(UserDetails userDetails) {

        if (userDetails == null) {
            throw new IllegalStateException(
                    "No student is logged in."
            );
        }

        Student student = studentService.findByEmail(
                userDetails.getUsername()
        );

        if (student == null) {
            throw new IllegalStateException(
                    "Student account not found."
            );
        }

        return student;
    }

    // OPEN STUDY TIMER
    @GetMapping("/timer")
    public String timer(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        model.addAttribute(
                "courses",
                courseService.getAllCourses(student)
        );

        model.addAttribute(
                "studentName",
                student.getFullName()
        );

        return "timer";
    }

    // SAVE COMPLETED STUDY SESSION
    @PostMapping("/timer/save")
    public String saveSession(
            @RequestParam Long courseId,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam Long durationSeconds,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            Student student = getCurrentStudent(userDetails);

            studySessionService.saveSession(
                    courseId,
                    student,
                    LocalDateTime.parse(startTime),
                    LocalDateTime.parse(endTime),
                    durationSeconds
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Your study session has been saved!"
            );

            return "redirect:/progress";

        } catch (Exception exception) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "Could not save your study session: "
                            + exception.getMessage()
            );

            return "redirect:/timer";
        }
    }
}
