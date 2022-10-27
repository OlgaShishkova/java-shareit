package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExistException;

import java.util.*;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        checkEmailExists(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        if (user.getName() != null) {
            users.get(user.getId()).setName(user.getName());
        }
        if (user.getEmail() != null) {
            checkEmailExists(user);
            users.get(user.getId()).setEmail(user.getEmail());
        }
        return users.get(user.getId());
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private void checkEmailExists(User user) {
        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new UserAlreadyExistException("Такой пользователь уже существует");
        }
    }
}
