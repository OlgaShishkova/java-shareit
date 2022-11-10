package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

public class BookingMapper {
    public static BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getStart(),
                booking.getEnd(),
                booking.getItem() != null ? booking.getItem().getId() : null,
                booking.getBooker() != null ? booking.getBooker().getId() : null,
                booking.getStatus(),
                booking.getItem().getName()
        );
    }

    public static Booking toBooking(BookingDto bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(new Item());
        if (bookingDto.getItemId() != null) {
            booking.getItem().setId(bookingDto.getItemId());
        }
        booking.setBooker(new User());
        if (bookingDto.getBookerId() != null) {
            booking.getBooker().setId(bookingDto.getBookerId());
        }
        booking.setStatus(bookingDto.getStatus());
        return booking;
    }
}
