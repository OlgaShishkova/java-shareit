package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
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
//        checkIfUserExists(item.getUserId());
        return itemRepository.add(item);
    }

    @Override
    public Item update(Long userId, Item item) {
//        checkIfUserExists(userId);
        return itemRepository.update(userId, item);
    }

    @Override
    public List<Item> getByUserId(Long userId) {
//        checkIfUserExists(userId);
        return itemRepository.getByUserId(userId);
    }

    @Override
    public Item getByItemId(Long itemId) {
        return itemRepository.getByItemId(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }

    @Override
    public void delete(Long userId, Long itemId) {
//        checkIfUserExists(userId);
        itemRepository.delete(userId, itemId);
    }

//    private void checkIfUserExists(Long userId) {
//        if (userRepository.getById(userId).isEmpty()) {
//            throw new UserNotFoundException("Пользователь не найден");
//        }
//    }
}
