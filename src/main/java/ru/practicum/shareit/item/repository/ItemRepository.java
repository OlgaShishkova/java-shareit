package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {
    Item add(Item item);

    Item update(Long userId, Item item);

    List<Item> getByUserId(Long userId);

    Optional<Item> getByItemId(Long itemId);

    List<Item> search(String text);

    void delete(Long userId, Long itemId);
}
