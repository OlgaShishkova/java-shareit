package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class BookingMapper {
    public static BookingDtoOutput toBookingDto(Booking booking) {
        return new BookingDtoOutput(
                booking.getId(),
                booking.getStart(),
                booking.getEnd(),
                ItemMapper.toItemDto(booking.getItem()),
                UserMapper.toUserDto(booking.getBooker()),
                booking.getStatus()
        );
    }

    public static Booking toBooking(BookingDtoInput bookingDto) {
        Booking booking = new Booking();
        booking.setStart(bookingDto.getStart());
        booking.setEnd(bookingDto.getEnd());
        booking.setItem(new Item());
        if (bookingDto.getItemId() != null) {
            booking.getItem().setId(bookingDto.getItemId());
        }
        booking.setBooker(new User());
        return booking;
    }

    public static List<BookingDtoOutput> toBookingDto(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    public static BookingDtoForItem toBookingDtoForItem(Booking booking) {
        return new BookingDtoForItem(
                booking.getId(),
                booking.getBooker().getId());
    }
}
