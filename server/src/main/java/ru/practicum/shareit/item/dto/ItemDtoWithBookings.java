package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDtoWithBookings {
    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private Long requestId;

    private BookingDtoForItem lastBooking;

    private BookingDtoForItem nextBooking;

    private List<CommentDto> comments;
}
