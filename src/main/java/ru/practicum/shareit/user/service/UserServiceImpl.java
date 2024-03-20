package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Override
    public List<UserDto> getAll() {
        return null;
    }

    @Override
    public UserDto create(UserDto userDto) {
        return null;
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        return null;
    }

    @Override
    public UserDto getUser(long userId) {
        return null;
    }

    @Override
    public void deleteUser(long userId) {
        // TODO document why this method is empty
    }
}
