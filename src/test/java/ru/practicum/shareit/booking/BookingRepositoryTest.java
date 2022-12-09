package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Test
    void testFindAllByBookerIdAndStartIsBeforeAndEndIsAfter() {
        User owner = userRepository.save(new User(null, "owner", "owner@email.ru"));
        User booker = userRepository.save(new User(null, "booker", "booker@email.ru"));
        Item item = itemRepository.save(new Item(
                null,
                "item",
                "description",
                true,
                owner,
                null
        ));
        LocalDateTime currentTime = LocalDateTime.now();
        Booking bookingPast = bookingRepository.save(new Booking(
                null,
                currentTime.minusDays(10),
                currentTime.minusDays(5),
                item,
                booker,
                Status.APPROVED
        ));
        Booking bookingCurrent = bookingRepository.save(new Booking(
                null,
                currentTime.minusDays(5),
                currentTime.plusDays(5),
                item,
                booker,
                Status.APPROVED
        ));
        Booking bookingFuture = bookingRepository.save(new Booking(
                null,
                currentTime.plusDays(10),
                currentTime.plusDays(15),
                item,
                booker,
                Status.WAITING
        ));
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(
                booker.getId(), currentTime, currentTime, Pageable.ofSize(10));
        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getId(), is(bookingCurrent.getId()));
    }
}