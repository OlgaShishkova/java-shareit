package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item);

    Item update(Long userId, Item item);

    List<ItemDtoWithBookings> findByUserId(Long userId);

    Item findByItemId(Long itemId);

    ItemDtoWithBookings findByItemId(Long userId, Long itemId);

    List<Item> search(String text);

    void delete(Long userId, Long itemId);
}
