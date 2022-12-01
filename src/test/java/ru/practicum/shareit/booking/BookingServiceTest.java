package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest
class BookingServiceTest {
    @Mock
    private  BookingRepository mockBookingRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private ItemService mockItemService;

    @Test
    void testFindByIdReturnsException() {
        BookingService bookingService = new BookingServiceImpl(mockBookingRepository, mockUserService, mockItemService);
        Mockito
                .when(mockBookingRepository.findById(1L))
                .thenReturn(Optional.empty());
        final BookingNotFoundException exception = Assertions.assertThrows(
                BookingNotFoundException.class,
                () -> bookingService.findById(1L));
        assertThat(exception.getMessage(), equalTo("Бронирование не найдено"));
    }

}