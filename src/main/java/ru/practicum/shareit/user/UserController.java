package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        List<User> users = userService.getAll();
        return users.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{id}")
    public UserDto getById(@PathVariable Long id) {
        return UserMapper.toUserDto(userService.getById(id));
    }

    @PostMapping
    public User create(@RequestBody @Valid UserDto userDto) {
        return userService.create(UserMapper.toUser(userDto));
    }

    @PatchMapping("{id}")
    public UserDto update(@PathVariable Long id, @RequestBody UserDto userDto) {
        userService.getById(id);
        userDto.setId(id);
        return UserMapper.toUserDto(userService.update(UserMapper.toUser(userDto)));
    }

    @DeleteMapping("{id}")
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
