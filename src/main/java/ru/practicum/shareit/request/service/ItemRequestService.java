package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;

import java.util.List;

public interface ItemRequestService {
    ItemRequest add(ItemRequest itemRequest);

    List<ItemRequestDtoWithItems> findByUserId(Long userId);

    ItemRequest findByRequestId(Long requestId);

    ItemRequestDtoWithItems findByRequestId(Long userId, Long requestId);

    List<ItemRequestDtoWithItems> findAll(Long userId, int from, int size);
}
