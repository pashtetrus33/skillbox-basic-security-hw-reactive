package ru.skillbox.task_tracker.repository;

import org.reactivestreams.Publisher;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.Task;

public interface TaskRepository extends ReactiveMongoRepository<Task, String> {
    Flux<Task> findByAssigneeId(String assigneeId);

    // Найти все задачи по authorId
    Flux<Task> findAllByAuthorId(String authorId);

    // Найти все задачи по assigneeId
    Flux<Task> findAllByAssigneeId(String assigneeId);

    // Найти все задачи, где observerIds содержит данный id
    Flux<Task> findAllByObserverIdsContaining(String observerId);
}
