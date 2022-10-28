package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
public class Item {
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private Boolean available;
    private Long userId;
    private ItemRequest request;
}
