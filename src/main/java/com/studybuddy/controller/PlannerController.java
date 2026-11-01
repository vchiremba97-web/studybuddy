
package com.studybuddy.controller;

import com.studybuddy.model.Student;
import com.studybuddy.model.StudyTask;
import com.studybuddy.service.PlannerService;
import com.studybuddy.service.StudentService;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;

@Controller
@RequestMapping("/planner")
public class PlannerController {

    private final PlannerService plannerService;
    private final StudentService studentService;

    public PlannerController(
            PlannerService plannerService,
            StudentService studentService) {

        this.plannerService = plannerService;
        this.studentService = studentService;
    }

    private Student getCurrentStudent(UserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalStateException("No student is logged in.");
        }

        Student student = studentService.findByEmail(userDetails.getUsername());

        if (student == null) {
            throw new IllegalStateException("Student account not found.");
        }

        return student;
    }

    private void addPlannerAttributes(Model model, Student student) {
        model.addAttribute("tasks", plannerService.getTasks(student.getId()));
        model.addAttribute("studentName", student.getFullName());
        model.addAttribute("today", LocalDate.now());
        model.addAttribute("tomorrow", LocalDate.now().plusDays(1));
    }

    @GetMapping
    public String planner(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        addPlannerAttributes(model, student);

        model.addAttribute("taskForm", new StudyTask());
        model.addAttribute("editing", false);

        return "planner";
    }

    @PostMapping
    public String addTask(
            @Valid @ModelAttribute("taskForm") StudyTask taskForm,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        if (bindingResult.hasErrors()) {
            addPlannerAttributes(model, student);
            model.addAttribute("editing", false);
            return "planner";
        }

        plannerService.addTask(student.getId(), taskForm);

        return "redirect:/planner";
    }

    @GetMapping("/{id}/edit")
    public String editTask(
            @PathVariable long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        StudyTask task = plannerService.getTask(student.getId(), id);

        if (task == null) {
            return "redirect:/planner";
        }

        addPlannerAttributes(model, student);

        model.addAttribute("taskForm", task);
        model.addAttribute("editing", true);
        model.addAttribute("editingTaskId", id);

        return "planner";
    }

    @PostMapping("/{id}/edit")
    public String updateTask(
            @PathVariable long id,
            @Valid @ModelAttribute("taskForm") StudyTask taskForm,
            BindingResult bindingResult,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        if (bindingResult.hasErrors()) {
            addPlannerAttributes(model, student);
            model.addAttribute("editing", true);
            model.addAttribute("editingTaskId", id);

            return "planner";
        }

        boolean updated = plannerService.updateTask(
                student.getId(), id, taskForm
        );

        if (!updated) {
            return "redirect:/planner";
        }

        return "redirect:/planner";
    }

    @PostMapping("/{id}/toggle")
    public String toggleTask(
            @PathVariable long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        plannerService.toggleTask(student.getId(), id);

        return "redirect:/planner";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(
            @PathVariable long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        Student student = getCurrentStudent(userDetails);

        plannerService.deleteTask(student.getId(), id);

        return "redirect:/planner";
    }
}