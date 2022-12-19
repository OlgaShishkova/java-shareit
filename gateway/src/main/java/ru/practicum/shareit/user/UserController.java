package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constraint.Create;
import ru.practicum.shareit.constraint.Update;
import ru.practicum.shareit.user.dto.UserDto;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> findById(@PathVariable Long userId) {
        return userClient.findById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Validated(Create.class) UserDto userDto) {
        return userClient.create(userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId,
                                             @RequestBody @Validated(Update.class) UserDto userDto) {
        return userClient.update(userId, userDto);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity<Object> delete(@PathVariable Long userId) {
        return userClient.delete(userId);
    }
}
