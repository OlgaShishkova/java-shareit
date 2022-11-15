package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public Item add(Item item) {
        checkIfUserExists(item.getOwner().getId());
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long userId, Item item) {
        checkIfUserExists(userId);
        Item itemToUpdate = itemRepository.findById(item.getId()).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
        if (itemToUpdate.getOwner().getId() != userId) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemRepository.save(itemToUpdate);
    }

    @Override
    public List<Item> findByUserId(Long userId) {
        checkIfUserExists(userId);
        return itemRepository.findAllByOwnerId(userId);
    }

    @Override
    public Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }

    @Override
    public void delete(Long userId, Long itemId) {
        checkIfUserExists(userId);
        itemRepository.deleteById(itemId);
    }

    private void checkIfUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
    }
}
