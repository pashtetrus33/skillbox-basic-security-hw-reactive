package ru.skillbox.task_tracker.service;

import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.web.model.UserRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;

import java.util.Set;

public interface UserService {

    Flux<UserResponse> findAll();

    Mono<UserResponse> findById(String id);

    Mono<UserResponse> create(UserRequest user);

    Mono<UserResponse> update(String id, UserRequest updatedUser);

    Mono<Void> deleteById(String id);

    Flux<User> findAllById(Set<String> observerIds);
}
