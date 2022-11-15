package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

public class BookingMapper {
    public static BookingDtoOutput toBookingDto(Booking booking) {
        return new BookingDtoOutput(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),// != null ? booking.getItem() : null,
                UserMapper.toUserDto(booking.getBooker()),// != null ? booking.getBooker() : null,
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoInput bookingDto) {
        Booking booking = new Booking();
//        booking.setId(bookingDto.getId());
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(new Item());
        if (bookingDto.getItemId() != null) {
            booking.getItem().setId(bookingDto.getItemId());
        }
        booking.setBooker(new User());
//        if (bookingDto.getBookerId() != null) {
//            booking.getBooker().setId(bookingDto.getBookerId());
//        }
//        booking.setStatus(bookingDto.getStatus());
        return booking;
    }

    public static List<BookingDtoOutput> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
