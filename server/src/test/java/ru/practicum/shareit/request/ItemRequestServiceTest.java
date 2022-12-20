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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
    private final ItemRequestService itemRequestService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final EntityManager em;

    @Test
    void testFindByRequestIdReturnsException() {
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
        LocalDateTime created = LocalDateTime.now();
        User user = userRepository.save(new User(
                null,
                "user",
                "user@email.ru")
        );
        ItemRequest itemRequest = itemRequestService.add(new ItemRequest(
                null,
                "item request",
                user,
                created)
        );

        TypedQuery<ItemRequest> query = em.createQuery(
                "Select i from ItemRequest i where i.id = :id", ItemRequest.class);
        ItemRequest addedItemRequest = query.setParameter("id", itemRequest.getId()).getSingleResult();

        assertThat(addedItemRequest.getId(), equalTo((itemRequest.getId())));
        assertThat(addedItemRequest.getDescription(), equalTo(itemRequest.getDescription()));
        assertThat(addedItemRequest.getRequestor().getId(), equalTo(itemRequest.getRequestor().getId()));
        assertThat(addedItemRequest.getCreated(), equalTo(itemRequest.getCreated()));
    }

    @Test
    void testFindByRequestId() {
        LocalDateTime created = LocalDateTime.now();
        User user = userRepository.save(new User(
                null,
                "user",
                "user@email.ru")
        );
        ItemRequest itemRequest = itemRequestRepository.save(new ItemRequest(
                null,
                "item request",
                user,
                created)
        );
        Item item = itemRepository.save(new Item(
                null,
                "item",
                "description",
                true,
                user,
                itemRequest)
        );
        ItemRequestDtoWithItems itemRequestDtoWithItems =
                itemRequestService.findByRequestId(user.getId(), itemRequest.getId());
        assertThat(itemRequestDtoWithItems.getId(), is(itemRequest.getId()));
        assertThat(itemRequestDtoWithItems.getItems(), hasSize(1));
        assertThat(itemRequestDtoWithItems.getItems().get(0).getId(), is(item.getId()));
    }

    @Test
    void testFindByUserId() {
        LocalDateTime created = LocalDateTime.now();
        User user = userRepository.save(new User(
                null,
                "user",
                "user@email.ru")
        );
        itemRequestRepository.save(new ItemRequest(
                null,
                "item request",
                user,
                created)
        );
        List<ItemRequestDtoWithItems> itemRequests =
                itemRequestService.findByUserId(user.getId());
        assertThat(itemRequests, hasSize(1));
    }

    @Test
    void testFindAll() {
        LocalDateTime created = LocalDateTime.now();
        User user = userRepository.save(new User(
                null,
                "user",
                "user@email.ru")
        );
        itemRequestRepository.save(new ItemRequest(
                null,
                "item request",
                user,
                created)
        );
        List<ItemRequestDtoWithItems> itemRequests =
                itemRequestService.findAll(user.getId(), 0, 10);
        assertThat(itemRequests, hasSize(0));
    }
}