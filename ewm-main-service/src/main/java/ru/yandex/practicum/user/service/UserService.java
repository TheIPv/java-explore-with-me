package ru.yandex.practicum.user.service;

import org.springframework.data.domain.PageRequest;
import ru.yandex.practicum.user.dto.UserDto;
import ru.yandex.practicum.user.model.NewUserRequest;
import ru.yandex.practicum.user.model.User;

import java.util.List;

public interface UserService {
    List<UserDto> getUsers(List<Long> ids, PageRequest pageable);
    UserDto addUser(NewUserRequest newUserRequest);
    void deleteUserById(Long userId);
    User get(Long id);
}
