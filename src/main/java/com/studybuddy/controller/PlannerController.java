
package com.studybuddy.controller;

import com.studybuddy.model.StudyTask;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Controller
@RequestMapping("/planner")
public class PlannerController {

    // Starter-only in-memory storage. Tasks reset when the application restarts.
    private final List<StudyTask> tasks = new ArrayList<>();
    private final AtomicLong ids = new AtomicLong(1);

    public PlannerController() {
        tasks.add(new StudyTask(
                ids.getAndIncrement(),
                "Review lecture notes",
                "Database Systems",
                LocalDate.now().plusDays(1),
                "High",
                false
        ));

        tasks.add(new StudyTask(
                ids.getAndIncrement(),
                "Prepare practice questions",
                "Software Engineering",
                LocalDate.now().plusDays(3),
                "Medium",
                false
        ));
    }

    @GetMapping
    public String planner(Model model) {
        model.addAttribute(
                "tasks",
                tasks.stream()
                        .sorted(Comparator.comparing(StudyTask::getDueDate))
                        .toList()
        );

        model.addAttribute("taskForm", new StudyTask());

        return "planner";
    }

    @PostMapping
    public String addTask(
            @Valid @ModelAttribute("taskForm") StudyTask taskForm,
            BindingResult bindingResult,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute(
                    "tasks",
                    tasks.stream()
                            .sorted(Comparator.comparing(StudyTask::getDueDate))
                            .toList()
            );

            return "planner";
        }

        tasks.add(new StudyTask(
                ids.getAndIncrement(),
                taskForm.getTitle(),
                taskForm.getSubject(),
                taskForm.getDueDate(),
                taskForm.getPriority(),
                false
        ));

        return "redirect:/planner";
    }

    @PostMapping("/{id}/toggle")
    public String toggleTask(@PathVariable long id) {
        tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst()
                .ifPresent(task -> task.setCompleted(!task.isCompleted()));

        return "redirect:/planner";
    }

    @PostMapping("/{id}/delete")
    public String deleteTask(@PathVariable long id) {
        tasks.removeIf(task -> task.getId() == id);

        return "redirect:/planner";
    }
}
