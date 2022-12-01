package ru.practicum.shareit.request;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.service.UserService;

import java.util.Optional;

@Transactional
@SpringBootTest
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private ItemRepository mockItemRepository;

    @Test
    void testFindByRequestIdReturnsException() {
        ItemRequestService itemRequestService =
                new ItemRequestServiceImpl(mockItemRequestRepository, mockUserService, mockItemRepository);
        Mockito
                .when(mockItemRequestRepository.findById(1L))
                .thenReturn(Optional.empty());
        final RequestNotFoundException exception = Assertions.assertThrows(
                RequestNotFoundException.class,
                () -> itemRequestService.findByRequestId(1L));
        Assertions.assertEquals("Запрос не найден", exception.getMessage());
    }
}