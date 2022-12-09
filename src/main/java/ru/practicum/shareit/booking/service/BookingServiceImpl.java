package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private  final BookingRepository bookingRepository;
    private final UserService userService;
    private  final ItemService itemService;

    @Transactional
    @Override
    public Booking add(Booking booking) {
        User booker = userService.findById(booking.getBooker().getId());
        booking.setBooker(booker);
        Item item = itemService.findByItemId(booking.getItem().getId());
        if (Objects.equals(item.getOwner().getId(), booker.getId())) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Эта вещь уже забронирована");
        }
        booking.setItem(item);
        return bookingRepository.save(booking);
    }

    @Transactional
    @Override
    public Booking approve(Long userId, Long bookingId, Boolean approved) {
        userService.findById(userId);
        Booking booking = findById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        Status currentStatus = booking.getStatus();
        if (currentStatus == Status.APPROVED || currentStatus == Status.REJECTED) {
            throw new StatusAlreadyChangedException("Статус уже изменен");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        return booking;
    }

    @Override
    public Booking get(Long userId, Long bookingId) {
        userService.findById(userId);
        Booking booking = findById(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId) &&
                !Objects.equals(booking.getBooker().getId(), userId)) {
            throw new BookingNotFoundException("Бронирование не найдено");
        }
        return booking;
    }

    @Override
    public List<Booking> getAll(Long userId, String state, Integer from, Integer size) {
        userService.findById(userId);
        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable bookingPage = getPage(from, size, Sort.by("start").descending());
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBookerId(userId, bookingPage);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                        userId, currentTime, currentTime, bookingPage);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndEndIsBefore(userId, currentTime, bookingPage);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndStartIsAfter(userId, currentTime, bookingPage);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndEndIsAfterAndStatusIs(
                        userId, currentTime, Status.WAITING, bookingPage);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndEndIsAfterAndStatusIs(
                        userId, currentTime, Status.REJECTED, bookingPage);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookings;
    }

    private static Pageable getPage(Integer from, Integer size, Sort sort) {
        int page = from / size;
        return PageRequest.of(page, size, sort);
    }

    @Override
    public List<Booking> getForAllItems(Long userId, String state, Integer from, Integer size) {
        userService.findById(userId);
        List<Booking> bookings;
        LocalDateTime currentTime = LocalDateTime.now();
        Pageable bookingPage = getPage(from, size, Sort.by("start").descending());
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByItemOwnerId(userId, bookingPage);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
                        userId, currentTime, currentTime, bookingPage);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, currentTime, bookingPage);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, currentTime, bookingPage);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStatusIs(
                        userId, currentTime, Status.WAITING, bookingPage);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByItemOwnerIdAndEndIsAfterAndStatusIs(
                        userId, currentTime, Status.REJECTED, bookingPage);
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
        return bookings;
    }

    @Override
    public Booking findById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new BookingNotFoundException("Бронирование не найдено"));
    }
}
