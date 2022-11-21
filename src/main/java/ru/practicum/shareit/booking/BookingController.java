package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.DateNotValidException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoOutput add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                @Valid @RequestBody BookingDtoInput bookingDto) {
        validateBookingDates(bookingDto);
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
        @RequestParam(defaultValue = "ALL") String state) {
        return BookingMapper.toBookingDto(bookingService.getAll(userId, state));
    }

    @GetMapping("owner")
    public List<BookingDtoOutput> getForAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        return BookingMapper.toBookingDto(bookingService.getForAllItems(userId, state));
    }

    private void validateBookingDates(BookingDtoInput bookingDto) {
        if (!bookingDto.getStart().isBefore(bookingDto.getEnd())) {
            throw new DateNotValidException("Дата окончания бронирования должна быть позже даты начала");
        }
    }
}