package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemIsNotAvailableException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.StatusAlreadyChangedException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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
    void get() {
    }

    @Test
    void getAll() {
    }

    @Test
    void getForAllItems() {
    }

}