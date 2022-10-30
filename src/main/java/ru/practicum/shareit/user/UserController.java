package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraint.Create;
import ru.practicum.shareit.constraint.Update;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        List<User> users = userService.getAll();
        return UserMapper.toUserDto(users);
    }

    @GetMapping("{id}")
    public UserDto getById(@PathVariable Long id) {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PostMapping
    @Validated(Create.class)
    public UserDto create(@RequestBody @Valid UserDto userDto) {
        return UserMapper.toUserDto(userService.create(UserMapper.toUser(userDto)));
    }

    @PatchMapping("{id}")
    @Validated(Update.class)
    public UserDto update(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        userService.getById(id);
        userDto.setId(id);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto)));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
