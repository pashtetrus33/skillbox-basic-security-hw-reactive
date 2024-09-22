package ru.skillbox.task_tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.ls.LSOutput;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.exception.EntityNotFoundException;
import ru.skillbox.task_tracker.mapper.UserMapper;
import ru.skillbox.task_tracker.repository.TaskRepository;
import ru.skillbox.task_tracker.repository.UserRepository;
import ru.skillbox.task_tracker.service.TaskService;
import ru.skillbox.task_tracker.service.UserService;
import ru.skillbox.task_tracker.web.model.UserRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;

    @Override
    public Flux<UserResponse> findAll() {
        return userRepository.findAll().map(userMapper::toDto);
    }

    @Override
    public Mono<UserResponse> findById(String id) {
        Mono<User> byId = userRepository.findById(id);
        return byId
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + id)))
                .map(userMapper::toDto);

    }


    @Override
    public Mono<UserResponse> create(UserRequest user) {
        return userRepository.save(userMapper.toEntity(user)).map(userMapper::toDto);
    }

    @Override
    public Mono<UserResponse> update(String id, UserRequest userDto) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + id)))
                .flatMap(user -> {
                    // Обновление существующего объекта `user` на основе данных из `userDto`
                    user.setName(userDto.getName());
                    user.setEmail(userDto.getEmail());
                    // Сохраняем обновленный объект
                    return userRepository.save(user);
                })
                .map(userMapper::toDto); // Преобразуем обновленного пользователя в DTO
    }


    @Override
    public Mono<Void> deleteById(String id) {
        return taskRepository.findAllByAuthorId(id)
                .concatWith(taskRepository.findAllByAssigneeId(id))
                .concatWith(taskRepository.findAllByObserverIdsContaining(id))
                .flatMap(task -> taskRepository.deleteById(task.getId()))  // Удаляем все найденные задачи
                .then(userRepository.deleteById(id));
    }


    @Override
    public Flux<User> findAllById(Set<String> observerIds) {
        // Возвращаем Flux<User> из репозитория, если он пустой, просто возвращаем пустой Flux
        return userRepository.findAllById(observerIds)
                .collectList() // Собираем все результаты в список
                .flatMapMany(users -> {
                    if (users.isEmpty()) {
                        // Логируем предупреждение о том, что пользователи не найдены
                        log.warn("No users found with ids: {}", observerIds);
                        // Возвращаем пустой Flux
                        return Flux.empty();
                    } else {
                        // Возвращаем Flux<User> из списка пользователей
                        return Flux.fromIterable(users);
                    }
                });
    }
}