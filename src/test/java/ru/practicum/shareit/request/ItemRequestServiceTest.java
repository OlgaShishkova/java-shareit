package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

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
class ItemRequestServiceTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private ItemRepository mockItemRepository;

    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;

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

    @Test
    void add() {
    }

    @Test
    void findByUserId() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(
                null,
                "user",
                "user@email.ru"
        );
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(
                null,
                "item request",
                user,
                created
        );
        itemRequestRepository.save(itemRequest);
        Item item = new Item(
                null,
                "item",
                "description",
                true,
                user,
                itemRequest
        );
        itemRepository.save(item);
        ItemRequestDtoWithItems itemRequestDtoWithItems =
                itemRequestService.findByRequestId(user.getId(), itemRequest.getId());
        assertThat(itemRequestDtoWithItems.getId(), is(itemRequest.getId()));
        assertThat(itemRequestDtoWithItems.getItems(), hasSize(1));
        assertThat(itemRequestDtoWithItems.getItems().get(0).getId(), is(item.getId()));
    }

    @Test
    void findAll() {
        LocalDateTime created = LocalDateTime.now();
        User user = new User(
                null,
                "user",
                "user@email.ru"
        );
        userRepository.save(user);
        ItemRequest itemRequest = new ItemRequest(
                null,
                "item request",
                user,
                created
        );
        itemRequestRepository.save(itemRequest);
        Item item = new Item(
                null,
                "item",
                "description",
                true,
                user,
                itemRequest
        );
        itemRepository.save(item);
        List<ItemRequestDtoWithItems> itemRequests =
                itemRequestService.findAll(user.getId(), 0, 10);
        assertThat(itemRequests, hasSize(0));
    }
}