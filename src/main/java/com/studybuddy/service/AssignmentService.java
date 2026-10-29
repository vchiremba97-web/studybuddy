
package com.studybuddy.service;

import com.studybuddy.model.Assignment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AssignmentService {

    private final List<Assignment> assignments = new ArrayList<>();
    private final AtomicLong nextId = new AtomicLong(1);

    public synchronized List<Assignment> getAll() {
        return new ArrayList<>(assignments);
    }

    public synchronized void add(String title, String course,
                                 String dueDate, String priority) {

        if (title == null || title.isBlank()
                || course == null || course.isBlank()
                || dueDate == null || dueDate.isBlank()) {
            return;
        }

        String cleanPriority =
                (priority == null || priority.isBlank())
                        ? "Medium"
                        : priority.trim();

        assignments.add(new Assignment(
                nextId.getAndIncrement(),
                title.trim(),
                course.trim(),
                dueDate.trim(),
                cleanPriority
        ));
    }
}
