package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        List<User> users = userService.findAll();
        return UserMapper.toUserDto(users);
    }

    @GetMapping("{id}")
    public UserDto findById(@PathVariable Long id) {
        return UserMapper.toUserDto(userService.findById(id));
    }

    @PostMapping
    public UserDto create(@RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }

    @PatchMapping("{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        user.setId(id);
        return UserMapper.toUserDto(userService.update(user));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
