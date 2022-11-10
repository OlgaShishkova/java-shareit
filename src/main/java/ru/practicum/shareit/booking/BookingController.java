package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody BookingDto bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.getBooker().setId(userId);
        return BookingMapper.toBookingDto(bookingService.add(booking));
    }
}