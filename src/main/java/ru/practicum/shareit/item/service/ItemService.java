package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item add(Item item);

    Item update(Long userId, Item item);

    List<ItemDtoWithBookings> findByUserId(Long userId, Integer from, Integer size);

    Item findByItemId(Long itemId);

    ItemDtoWithBookings findByItemId(Long userId, Long itemId);

    List<Item> search(String text, Integer from, Integer size);

    void delete(Long userId, Long itemId);

    Comment addComment(Long userId, Long itemId, Comment comment);
}
