package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @Valid @RequestBody BookingDtoInput bookingDto) {
        Booking booking = BookingMapper.toBooking(bookingDto);
        booking.getBooker().setId(userId);
        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDto(bookingService.add(booking));
    }

    @PatchMapping("{bookingId}")
    public BookingDtoOutput approve(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId, @RequestParam Boolean approved) {
        return BookingMapper.toBookingDto(bookingService.approve(userId, bookingId, approved));
    }

    @GetMapping("{bookingId}")
    public BookingDtoOutput get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        return BookingMapper.toBookingDto(bookingService.get(userId, bookingId));
    }

    @GetMapping
    public List<BookingDtoOutput> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state,
                                         @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return BookingMapper.toBookingDto(bookingService.getAll(userId, state, from, size));
    }

    @GetMapping("owner")
    public List<BookingDtoOutput> getForAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @RequestParam(defaultValue = "ALL") String state,
                                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return BookingMapper.toBookingDto(bookingService.getForAllItems(userId, state, from, size));
    }
}