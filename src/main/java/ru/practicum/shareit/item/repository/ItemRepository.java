package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item add(Item item);
    Item update(Long userId, Item item);
    List<Item> getByUserId(Long userId);
    void delete(Long userId, Long itemId);
}
