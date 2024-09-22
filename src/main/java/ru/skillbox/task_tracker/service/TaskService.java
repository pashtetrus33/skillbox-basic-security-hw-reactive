package ru.skillbox.task_tracker.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.web.model.TaskRequest;
import ru.skillbox.task_tracker.web.model.TaskResponse;

public interface TaskService {

    Flux<TaskResponse> findAll();

    Mono<TaskResponse> findById(String id);

    Mono<TaskResponse> create(TaskRequest task);

    Mono<TaskResponse> update(String id, TaskRequest updatedTask);

    Mono<Void> deleteById(String id);

    Mono<TaskResponse> addObserver(String id, String observerId);
}
