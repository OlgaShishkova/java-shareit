package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item);

    Item update(Long userId, Item item);

    List<Item> findByUserId(Long userId);

    Item findByItemId(Long itemId);

    List<Item> search(String text);

    void delete(Long userId, Long itemId);
}
