package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

@Transactional
@SpringBootTest
public class ItemServiceTest {
    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private BookingRepository mockBookingRepository;

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private ItemRequestService mockItemRequestService;

    @Test
    void testFindByItemIdReturnsException() {
        ItemService itemService = new ItemServiceImpl(mockItemRepository, mockBookingRepository,
                mockCommentRepository, mockUserService, mockItemRequestService);
        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.empty());
        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.findByItemId(1L));
        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }
}
