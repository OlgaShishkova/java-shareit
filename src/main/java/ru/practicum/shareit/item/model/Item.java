package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Getter
public class Item {
    private long id;
    @NotBlank
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;
}
