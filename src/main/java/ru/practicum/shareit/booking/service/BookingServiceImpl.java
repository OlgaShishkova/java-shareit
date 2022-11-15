package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private  final BookingRepository bookingRepository;
    private final UserService userService;
    private  final ItemService itemService;

    @Override
    public Booking add(Booking booking) {
        User booker = userService.findById(booking.getBooker().getId());
        booking.setBooker(booker);
        Item item = itemService.findByItemId(booking.getItem().getId());
        if (item.getOwner().getId() == booker.getId()) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Эта вещь уже забронирована");
        }
        booking.setItem(item);
        return bookingRepository.save(booking);
    }

    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        userService.findById(userId);
        Booking booking = findById(bookingId);
        if (booking.getItem().getOwner().getId() != userId) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        if(approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return bookingRepository.save(booking);
    }

    @Override
    public Booking get(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = findById(bookingId);
        if (booking.getItem().getOwner().getId() != userId &&
            booking.getBooker().getId() != userId) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        return booking;
    }

    @Override
    public List<Booking> getAll(Long userId, String state) {
        userService.findById(userId);
        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        currentTime, currentTime);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByStartDesc(userId, currentTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByStartDesc(userId, currentTime);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndEndIsAfterAndStatusIsOrderByStartDesc(
                        userId, currentTime, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndEndIsAfterAndStatusIsOrderByStartDesc(
                        userId, currentTime, Status.REJECTED);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookings;
    }

    @Override
    public List<Booking> getForAllItems(Long userId, String state) {
        userService.findById(userId);
        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerIdOrderByStartDesc(userId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(userId,
                        currentTime, currentTime);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(userId, currentTime);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(userId, currentTime);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStatusIsOrderByStartDesc(
                        userId, currentTime, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStatusIsOrderByStartDesc(
                        userId, currentTime, Status.REJECTED);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookings;
    }

    private Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
    }
}
