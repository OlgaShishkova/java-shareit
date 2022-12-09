package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;

import java.util.List;

public interface BookingService {
    Booking add(Booking booking);

    Booking approve(Long userId, Long bookingId, Boolean approved);

    Booking get(Long userId, Long bookingId);

    Booking findById(Long id);

    List<Booking> getAll(Long userId, String state, Integer from, Integer size);

    List<Booking> getForAllItems(Long userId, String state, Integer from, Integer size);
}