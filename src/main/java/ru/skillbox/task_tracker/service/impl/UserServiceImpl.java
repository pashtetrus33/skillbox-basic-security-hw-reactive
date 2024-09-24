package ru.skillbox.task_tracker.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.skillbox.task_tracker.entity.RoleType;
import ru.skillbox.task_tracker.entity.User;
import ru.skillbox.task_tracker.exception.EntityNotFoundException;
import ru.skillbox.task_tracker.mapper.UserMapper;
import ru.skillbox.task_tracker.repository.TaskRepository;
import ru.skillbox.task_tracker.repository.UserRepository;
import ru.skillbox.task_tracker.service.UserService;
import ru.skillbox.task_tracker.web.model.UserRequest;
import ru.skillbox.task_tracker.web.model.UserResponse;
import ru.skillbox.task_tracker.web.model.UserUpdateRequest;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Flux<UserResponse> findAll() {

        return userRepository.findAll().map(userMapper::toDto);
    }

    @Override
    public Mono<UserResponse> findById(String id) {

        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + id)))
                .map(userMapper::toDto);

    }


    @Override
    public Mono<UserResponse> create(UserRequest userRequest, RoleType roleType) {
        User user = userMapper.toEntity(userRequest);

        // Кодируем пароль
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Добавляем роль к пользователю
        user.getRoles().add(roleType); // Используем RoleType вместо объекта Role

        return userRepository.save(user)
                .map(userMapper::toDto);
    }


    @Override
    public Mono<UserResponse> update(String id, UserUpdateRequest userDto) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found with id: " + id)))
                .flatMap(user -> {
                    // Обновление существующего объекта `user` на основе данных из `userDto`
                    if (userDto.getUsername() != null) {
                        user.setUsername(userDto.getUsername());
                    }

                    if (userDto.getPassword() != null) {
                        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
                    }

                    if (userDto.getEmail() != null) {
                        user.setEmail(userDto.getEmail());
                    }

                    // Сохраняем обновленный объект
                    return userRepository.save(user);
                })
                .map(userMapper::toDto); // Преобразуем обновленного пользователя в DTO
    }


    @Override
    public Mono<Void> deleteById(String id) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    // Если пользователь существует, продолжаем с удалением задач
                    return taskRepository.findAllByAuthorId(id)
                            .concatWith(taskRepository.findAllByAssigneeId(id))
                            .concatWith(taskRepository.findAllByObserverIdsContaining(id))
                            .flatMap(task -> taskRepository.deleteById(task.getId()))  // Удаляем все найденные задачи
                            .then(userRepository.deleteById(id));
                })
                .switchIfEmpty(Mono.error(new EntityNotFoundException("User not found")));  // Обработка случая, когда пользователь не найден
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

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}