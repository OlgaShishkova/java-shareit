package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long userId, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
            Long bookerId, LocalDateTime time1, LocalDateTime time2, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByBookerIdAndEndIsAfterAndStatusIs(
            Long bookerId, LocalDateTime currentTime, Status status, Sort sort);

    List<Booking> findAllByItemOwnerId(Long ownerId, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(
            Long ownerId, LocalDateTime time1, LocalDateTime time2, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndStartIsAfter(Long ownerId, LocalDateTime currentTime, Sort sort);

    List<Booking> findAllByItemOwnerIdAndEndIsAfterAndStatusIs(
            Long ownerId, LocalDateTime currentTime, Status status, Sort sort);

    List<Booking> findAllByItemId(Long itemId);

    List<Booking> findAllByItemIn(Collection<Item> items);
}

