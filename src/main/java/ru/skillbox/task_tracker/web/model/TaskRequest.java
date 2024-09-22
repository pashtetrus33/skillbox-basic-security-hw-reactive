package ru.skillbox.task_tracker.web.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.task_tracker.entity.TaskStatus;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    private String name;

    private String description;

    private String authorId;

    private String assigneeId;

    private Set<String> observerIds;

    private TaskStatus status;
}
