package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    @Mock
    private ItemRepository mockItemRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemService itemService;
    private final EntityManager em;

    @Test
    void testFindByItemIdReturnsException() {
        Mockito
                .when(mockItemRepository.findById(1L))
                .thenReturn(Optional.empty());
        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.findByItemId(1L));
        Assertions.assertEquals("Вещь не найдена", exception.getMessage());
    }

    @Test
    void testAdd() {
        User user = new User(null, "user", "user@email.ru");
        userRepository.save(user);
        User requestor = new User(null, "requestor", "requestor@email.ru");
        userRepository.save(requestor);
        ItemRequest itemRequest = new ItemRequest(null, "itemRequest", requestor, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item = new Item(null, "item", "description", true, user, itemRequest);
        Item itemToAdd = itemService.add(item);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item addedItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(addedItem.getId(), equalTo((itemToAdd.getId())));
        assertThat(addedItem.getName(), equalTo(itemToAdd.getName()));
        assertThat(addedItem.getOwner().getId(), equalTo(itemToAdd.getOwner().getId()));
        assertThat(addedItem.getDescription(), equalTo(itemToAdd.getDescription()));
        assertThat(addedItem.getAvailable(), equalTo(itemToAdd.getAvailable()));
        assertThat(addedItem.getOwner(), equalTo(user));
        assertThat(addedItem.getRequest(), equalTo(itemRequest));
    }

    @Test
    void testUpdateItemName() {
        User user = new User(null, "user", "user@email.ru");
        userRepository.save(user);
        Item item = new Item(null, "item", "description", true, user, null);
        itemRepository.save(item);
        Item itemToUpdate = new Item(item.getId(), "updatedItem", null, null, null, null);
        itemService.update(user.getId(), itemToUpdate);

        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item updatedItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(updatedItem.getId(), equalTo(itemToUpdate.getId()));
        assertThat(updatedItem.getName(), equalTo("updatedItem"));
        assertThat(updatedItem.getDescription(), equalTo("description"));
        assertThat(updatedItem.getAvailable(), equalTo(true));
        assertThat(updatedItem.getOwner(), equalTo(user));
    }

    @Test
    void testUpdateItemDescription() {
        User user = new User(null, "user", "user@email.ru");
        userRepository.save(user);
        Item item = new Item(null, "item", "description", true, user, null);
        itemRepository.save(item);
        Item itemToUpdate = new Item(item.getId(), null, "updatedDescription", null, null, null);
        itemService.update(user.getId(), itemToUpdate);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item updatedItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(updatedItem.getId(), equalTo(itemToUpdate.getId()));
        assertThat(updatedItem.getName(), equalTo("item"));
        assertThat(updatedItem.getDescription(), equalTo("updatedDescription"));
        assertThat(updatedItem.getAvailable(), equalTo(true));
        assertThat(updatedItem.getOwner(), equalTo(user));
    }

    @Test
    void testUpdateItemAvailable() {
        User user = new User(null, "user", "user@email.ru");
        userRepository.save(user);
        Item item = new Item(null, "item", "description", true, user, null);
        itemRepository.save(item);
        Item itemToUpdate = new Item(item.getId(), null, null, false, null, null);
        itemService.update(user.getId(), itemToUpdate);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item updatedItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(updatedItem.getId(), equalTo(itemToUpdate.getId()));
        assertThat(updatedItem.getName(), equalTo("item"));
        assertThat(updatedItem.getDescription(), equalTo("description"));
        assertThat(updatedItem.getAvailable(), equalTo(false));
        assertThat(updatedItem.getOwner(), equalTo(user));
    }

    @Test
    void testUpdateItem() {
        User user = new User(null, "user", "user@email.ru");
        User requestor = new User(null, "requestor", "requestor@email.ru");
        userRepository.save(user);
        userRepository.save(requestor);
        ItemRequest itemRequest = new ItemRequest(null, "itemRequest", requestor, LocalDateTime.now());
        itemRequestRepository.save(itemRequest);
        Item item = new Item(null, "item", "description", true, user, itemRequest);
        itemRepository.save(item);
        Item itemToUpdate = new Item(item.getId(), "updatedItem", "updatedDescription", false,
                null, null);
        itemService.update(user.getId(), itemToUpdate);
        TypedQuery<Item> query = em.createQuery("Select i from Item i where i.id = :id", Item.class);
        Item updatedItem = query.setParameter("id", item.getId()).getSingleResult();

        assertThat(updatedItem.getId(), equalTo(itemToUpdate.getId()));
        assertThat(updatedItem.getName(), equalTo("updatedItem"));
        assertThat(updatedItem.getDescription(), equalTo("updatedDescription"));
        assertThat(updatedItem.getAvailable(), equalTo(false));
        assertThat(updatedItem.getOwner(), equalTo(user));
        assertThat(updatedItem.getRequest(), equalTo(itemRequest));
    }

    @Test
    void testUpdateItemWithWrongOwner() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(null, "item", "description", true, user1, null);
        itemRepository.save(item);
        Item itemToUpdate = new Item(item.getId(), "updatedItem", "updatedDescription", false,
                null, null);

        Assertions.assertThrows(ItemNotFoundException.class,
                () -> itemService.update(user2.getId(), itemToUpdate));
    }

}
