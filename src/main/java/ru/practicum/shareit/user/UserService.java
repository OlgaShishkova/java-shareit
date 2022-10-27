package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.create(user);
    }

    public User update(User user) {
        return userRepository.update(user);
    }

    public List<User> getAll() {
        return userRepository.getAll();
    }

    public User getById(Long id) {
        return userRepository.getById(id).orElseThrow(()->
                new UserNotFoundException("Пользователь не найден"));
    }

    public void delete(Long id) {
        userRepository.delete(id);
    }
}
