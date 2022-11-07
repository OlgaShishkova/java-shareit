/*
package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserAlreadyExistException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

public class UserRepositoryImpl implements UserRepository {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 0L;

    @Override
    public User create(User user) {
        checkIfEmailExists(user);
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
            checkIfEmailExists(user);
            users.get(user.getId()).setEmail(user.getEmail());
        }
        return users.get(user.getId());
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
    }

    private void checkIfEmailExists(User user) {
        if (users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()))) {
            throw new UserAlreadyExistException("Такой пользователь уже существует");
        }
    }
}
*/
