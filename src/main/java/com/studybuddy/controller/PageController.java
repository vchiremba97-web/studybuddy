
package com.studybuddy.controller;

import com.studybuddy.service.AssignmentService;
import com.studybuddy.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {

    private final CourseService courseService;
    private final AssignmentService assignmentService;

    public PageController(CourseService courseService,
                          AssignmentService assignmentService) {
        this.courseService = courseService;
        this.assignmentService = assignmentService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        model.addAttribute("assignments", assignmentService.getAll());
        model.addAttribute("courseCount", courseService.getAllCourses().size());
        model.addAttribute("assignmentCount", assignmentService.getAll().size());

        return "dashboard";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());

        return "courses";
    }

    @GetMapping("/assignments")
    public String assignments(Model model) {
        model.addAttribute("assignments", assignmentService.getAll());

        return "assignments";
    }




    @GetMapping("/progress")
    public String progress(Model model) {
        model.addAttribute("courseCount", courseService.getAllCourses().size());
        model.addAttribute("assignmentCount", assignmentService.getAll().size());

        return "progress";
    }

    @PostMapping("/courses/add")
    public String addCourse(
            @RequestParam String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String instructor,
            @RequestParam(required = false) String description) {

        courseService.addCourse(name, code, instructor, description);

        return "redirect:/courses";
    }

    @PostMapping("/courses/delete")
    public String deleteCourse(@RequestParam Long id) {
        courseService.deleteCourse(id);

        return "redirect:/courses";
    }

    @PostMapping("/assignments/add")
    public String addAssignment(
            @RequestParam String title,
            @RequestParam String course,
            @RequestParam String dueDate,
            @RequestParam(required = false) String priority) {

        assignmentService.add(title, course, dueDate, priority);

        return "redirect:/assignments";
    }
}
