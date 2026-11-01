
package com.studybuddy.service;

import com.studybuddy.model.StudyTask;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PlannerService {

    private final Map<Long, List<StudyTask>> tasksByStudent =
            new ConcurrentHashMap<>();

    private final AtomicLong ids = new AtomicLong(1);

    public List<StudyTask> getTasks(Long studentId) {
        return tasksFor(studentId).stream()
                .sorted(Comparator.comparing(StudyTask::getDueDate))
                .toList();
    }

    public void addTask(Long studentId, StudyTask form) {
        tasksFor(studentId).add(new StudyTask(
                ids.getAndIncrement(),
                form.getTitle(),
                form.getSubject(),
                form.getDueDate(),
                form.getPriority(),
                false
        ));
    }

    public StudyTask getTask(Long studentId, long taskId) {
        return tasksFor(studentId).stream()
                .filter(task -> task.getId() == taskId)
                .findFirst()
                .map(task -> new StudyTask(
                        task.getId(),
                        task.getTitle(),
                        task.getSubject(),
                        task.getDueDate(),
                        task.getPriority(),
                        task.isCompleted()
                ))
                .orElse(null);
    }

    // UPDATE EXISTING STUDY TASK
    public boolean updateTask(Long studentId, long taskId,
                              StudyTask updatedForm) {

        return tasksFor(studentId).stream()
                .filter(task -> task.getId() == taskId)
                .findFirst()
                .map(task -> {
                    task.setTitle(updatedForm.getTitle());
                    task.setSubject(updatedForm.getSubject());
                    task.setDueDate(updatedForm.getDueDate());
                    task.setPriority(updatedForm.getPriority());

                    // Preserve existing completion status
                    return true;
                })
                .orElse(false);
    }

    // MARK TASK AS COMPLETE OR INCOMPLETE
    public void toggleTask(Long studentId, long taskId) {
        tasksFor(studentId).stream()
                .filter(task -> task.getId() == taskId)
                .findFirst()
                .ifPresent(task ->
                        task.setCompleted(!task.isCompleted()));
    }

    // DELETE STUDY TASK
    public void deleteTask(Long studentId, long taskId) {
        tasksFor(studentId).removeIf(
                task -> task.getId() == taskId
        );
    }

    private List<StudyTask> tasksFor(Long studentId) {
        return tasksByStudent.computeIfAbsent(studentId, key -> {

            List<StudyTask> starter = new CopyOnWriteArrayList<>();

            starter.add(new StudyTask(
                    ids.getAndIncrement(),
                    "Review lecture notes",
                    "Database Systems",
                    LocalDate.now().plusDays(1),
                    "High",
                    false
            ));

            starter.add(new StudyTask(
                    ids.getAndIncrement(),
                    "Prepare practice questions",
                    "Software Engineering",
                    LocalDate.now().plusDays(3),
                    "Medium",
                    false
            ));

            return starter;
        });
    }
}