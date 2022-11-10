package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private  final BookingRepository bookingRepository;

    @Override
    public Booking add(Booking booking) {
        return bookingRepository.save(booking);
    }
}
