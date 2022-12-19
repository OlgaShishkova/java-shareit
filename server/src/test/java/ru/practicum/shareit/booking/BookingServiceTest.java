package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceTest {
    @Mock
    private final BookingRepository mockBookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    @Test
    void testFindByIdReturnsException() {
        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findById(1L));
        assertThat(exception.getMessage(), equalTo("Бронирование не найдено"));
    }

    @Test
    void testAddBookingByOwnerWithException() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(
                null,
                "item",
                "item description",
                true,
                user1,
                null
        );
        itemRepository.save(item);
        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user1,
                Status.WAITING
                );

        Assertions.assertThrows(ItemNotFoundException.class,
                () -> bookingService.add(booking));
    }

    @Test
    void testAddBookingNotAvailableWithException() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(
                null,
                "item",
                "item description",
                false,
                user1,
                null
        );
        itemRepository.save(item);
        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user2,
                Status.WAITING
        );

        Assertions.assertThrows(ItemIsNotAvailableException.class,
                () -> bookingService.add(booking));
    }

    @Test
    void testApproveStatusRejectedWithException() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(
                null,
                "item",
                "item description",
                false,
                user1,
                null
        );
        itemRepository.save(item);
        Booking booking = new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user2,
                Status.REJECTED
        );
        bookingRepository.save(booking);

        Assertions.assertThrows(StatusAlreadyChangedException.class,
                () -> bookingService.approve(user1.getId(), booking.getId(), true));
    }

    @Test
    void testApproveSetRejected() {
        User user1 = userRepository.save(new User(null, "user1", "user1@email.ru"));
        User user2 = userRepository.save(new User(null, "user2", "user2@email.ru"));
        Item item = itemRepository.save(new Item(
                null,
                "item",
                "item description",
                false,
                user1,
                null)
        );
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user2,
                Status.WAITING)
        );
        Booking bookingApproved = bookingService.approve(user1.getId(), booking.getId(), false);
        assertThat(bookingApproved.getStatus(), is(Status.REJECTED));
    }

    @Test
    void testGetByWrongUserWithException() {
        User user1 = userRepository.save(new User(null, "user1", "user1@email.ru"));
        User user2 = userRepository.save(new User(null, "user2", "user2@email.ru"));
        User user3 = userRepository.save(new User(null, "user3", "user3@email.ru"));
        Item item = itemRepository.save(new Item(
                null,
                "item",
                "item description",
                true,
                user1,
                null)
        );
        Booking booking = bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user2,
                Status.WAITING)
        );
        Assertions.assertThrows(BookingNotFoundException.class,
                () -> bookingService.get(user3.getId(), booking.getId()));
    }

    @Test
    void testGetAllUnsupportedStatusException() {
        User user = userRepository.save(new User(null, "user", "user@email.ru"));
        Assertions.assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getAll(user.getId(), "unsupported", 0, 10));
    }

    @Test
    void testGetAll() {
        User user1 = userRepository.save(new User(null, "user1", "user1@email.ru"));
        User user2 = userRepository.save(new User(null, "user2", "user2@email.ru"));
        Item item = itemRepository.save(new Item(
                null,
                "item",
                "item description",
                true,
                user1,
                null)
        );
        // future bookings
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user2,
                Status.WAITING)
        );
        // current bookings
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(3),
                item,
                user2,
                Status.REJECTED)
        );
        List<Booking> bookingsAll = bookingService.getAll(user2.getId(), "ALL", 0, 10);
        assertThat(bookingsAll, hasSize(2));
        List<Booking> bookingsPast = bookingService.getAll(user2.getId(), "PAST", 0, 10);
        assertThat(bookingsPast, hasSize(0));
        List<Booking> bookingsFuture = bookingService.getAll(user2.getId(), "FUTURE", 0, 10);
        assertThat(bookingsFuture, hasSize(1));
        List<Booking> bookingsCurrent = bookingService.getAll(user2.getId(), "CURRENT", 0, 10);
        assertThat(bookingsCurrent, hasSize(1));
        List<Booking> bookingsRejected = bookingService.getAll(user2.getId(), "REJECTED", 0, 10);
        assertThat(bookingsRejected, hasSize(1));
        List<Booking> bookingsWaiting = bookingService.getAll(user2.getId(), "WAITING", 0, 10);
        assertThat(bookingsWaiting, hasSize(1));
    }

    @Test
    void testGetForAllItemsUnsupportedStatusException() {
        User user = userRepository.save(new User(null, "user", "user@email.ru"));
        Assertions.assertThrows(UnsupportedStatusException.class,
                () -> bookingService.getForAllItems(user.getId(), "unsupported", 0, 10));
    }

    @Test
    void testGetForAllItems() {
        User user1 = userRepository.save(new User(null, "user1", "user1@email.ru"));
        User user2 = userRepository.save(new User(null, "user2", "user2@email.ru"));
//        User user3 = userRepository.save(new User(null, "user3", "user3@email.ru"));
        Item item = itemRepository.save(new Item(
                null,
                "item",
                "item description",
                true,
                user1,
                null)
        );
        // future bookings
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().plusDays(5),
                LocalDateTime.now().plusDays(10),
                item,
                user2,
                Status.WAITING)
        );
        // current bookings
        bookingRepository.save(new Booking(
                null,
                LocalDateTime.now().minusDays(3),
                LocalDateTime.now().plusDays(3),
                item,
                user2,
                Status.REJECTED)
        );
        List<Booking> bookingsAll = bookingService.getForAllItems(user1.getId(), "ALL", 0, 10);
        assertThat(bookingsAll, hasSize(2));
        List<Booking> bookingsPast = bookingService.getForAllItems(user1.getId(), "PAST", 0, 10);
        assertThat(bookingsPast, hasSize(0));
        List<Booking> bookingsFuture = bookingService.getForAllItems(user1.getId(), "FUTURE", 0, 10);
        assertThat(bookingsFuture, hasSize(1));
        List<Booking> bookingsCurrent = bookingService.getForAllItems(user1.getId(), "CURRENT", 0, 10);
        assertThat(bookingsCurrent, hasSize(1));
        List<Booking> bookingsRejected = bookingService.getForAllItems(user1.getId(), "REJECTED", 0, 10);
        assertThat(bookingsRejected, hasSize(1));
        List<Booking> bookingsWaiting = bookingService.getForAllItems(user1.getId(), "WAITING", 0, 10);
        assertThat(bookingsWaiting, hasSize(1));
    }
}