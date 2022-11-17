package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerIdOrderByStartDesc(Long userId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long bookerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime currentTime);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStatusIsOrderByStartDesc(
            Long bookerId, LocalDateTime currentTime, Status status);

    List<Booking> findAllByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(
            Long ownerId, LocalDateTime time1, LocalDateTime time2);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime currentTime);

    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime currentTime);

    List<Booking> findAllByItemOwnerIdAndEndIsAfterAndStatusIsOrderByStartDesc(
            Long ownerId, LocalDateTime currentTime, Status status);

       List<Booking> findAllByItemId(Long itemId);
}

