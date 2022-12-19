package ru.practicum.shareit.request.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;

@UtilityClass
public class ItemRequestMapper {
    public static ItemRequest toRequest(ItemRequestDtoInput itemRequestDtoInput) {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription(itemRequestDtoInput.getDescription());
        itemRequest.setRequestor(new User());
        return itemRequest;
    }

    public static ItemRequestDtoInput toRequestDtoInput(ItemRequest itemRequest) {
        return new ItemRequestDtoInput(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated()
        );
    }

    public static ItemRequestDtoWithItems toItemRequestDtoWithItems(ItemRequest itemRequest) {
        ItemRequestDtoWithItems itemRequestDtoWithItems = new ItemRequestDtoWithItems();
        itemRequestDtoWithItems.setId(itemRequest.getId());
        itemRequestDtoWithItems.setDescription(itemRequest.getDescription());
        itemRequestDtoWithItems.setCreated(itemRequest.getCreated());
        itemRequestDtoWithItems.setItems(new ArrayList<>());
        return itemRequestDtoWithItems;
    }
}
