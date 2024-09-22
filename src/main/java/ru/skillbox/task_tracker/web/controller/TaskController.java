package ru.skillbox.task_tracker.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;;
import ru.skillbox.task_tracker.service.TaskService;
import ru.skillbox.task_tracker.web.model.TaskRequest;
import ru.skillbox.task_tracker.web.model.TaskResponse;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public Flux<ResponseEntity<TaskResponse>> getAllTasks() {
        return taskService.findAll()
                .map(ResponseEntity::ok);
    }


    @GetMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> getTaskById(@PathVariable String id) {
        return taskService.findById(id)
                .map(ResponseEntity::ok);
    }

    @PostMapping
    public Mono<ResponseEntity<TaskResponse>> createTask(@RequestBody TaskRequest task) {
        return taskService.create(task)
                .map(ResponseEntity::ok);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<TaskResponse>> updateTask(@PathVariable String id, @RequestBody TaskRequest task) {
        return taskService.update(id, task)
                .map(ResponseEntity::ok);
    }

    @PatchMapping("/observers/{id}")
    public Mono<ResponseEntity<TaskResponse>> addObserver(@PathVariable String id, @RequestParam String observerId) {
        return taskService.addObserver(id, observerId)
                .map(ResponseEntity::ok);
    }


    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable String id) {
        return taskService.deleteById(id)
                .then(Mono.just(ResponseEntity.noContent().build()));
    }
}