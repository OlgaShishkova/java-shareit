package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public Item add(Item item) {
        checkIfUserExists(item.getUserId());
        return itemRepository.add(item);
    }

    public Item update(Long userId, Item item) {
        checkIfUserExists(userId);
        return itemRepository.update(userId, item);
    }

    public List<Item> getItems(Long userId) {
        checkIfUserExists(userId);
        return itemRepository.getByUserId(userId);
    }

    public void delete(Long userId, Long itemId) {
        checkIfUserExists(userId);
        itemRepository.delete(userId, itemId);
    }

    private void checkIfUserExists(Long userId) {
        if(userRepository.getById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
