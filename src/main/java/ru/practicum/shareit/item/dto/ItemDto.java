package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;

@Service
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private String name;
    private String description;
    private boolean available;
    private long requestId;
}
