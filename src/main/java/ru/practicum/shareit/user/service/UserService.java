package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Transactional
public interface UserService {

    @Transactional(readOnly = true)
    List<UserDto> getAll();

    UserDto create(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    @Transactional(readOnly = true)
    UserDto getUser(long userId);

    @Transactional(readOnly = true)
    UserDto getUser2(long userId);

    void deleteUser(long userId);

    @Transactional(readOnly = true)
    User findUser(long userId);

}