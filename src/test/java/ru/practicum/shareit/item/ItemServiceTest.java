package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotAuthorisedRequestException;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
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
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    private final BookingRepository bookingRepository;
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

    @Test
    void testFindByItemId() {
        User user = new User(1L, "user", "user@email.ru");
        userRepository.save(user);
        Item item1 = new Item(null, "item1", "item1Description", true, user, null);
        Item item2 = new Item(null, "item2", "item2Description", false, user, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        Item itemFound = itemService.findByItemId(item1.getId());

        assertThat(itemFound, equalTo(item1));
    }

    @Test
    void testSearch() {
        User user = new User(null, "user", "user@email.ru");
        userRepository.save(user);
        Item item1 = new Item(null, "Мяч", "item1Description", true, user, null);
        Item item2 = new Item(null, "item2", "насос для мяча", true, user, null);
        Item item3 = new Item(null, "Мяч", "item3Description", false, user, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemRepository.save(item3);
        List<Item> items = itemService.search("мяч", 0, 10);

        assertThat(items, hasSize(2));
        assertThat(items, allOf(hasItem(item1), hasItem(item2)));
    }

    @Test
    void testDelete() {
        User user = new User(null, "user", "user@email.ru");
        userRepository.save(user);
        Item item1 = new Item(null, "item1", "item1Description", true, user, null);
        Item item2 = new Item(null, "item2", "item2Description", true, user, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        itemService.delete(user.getId(), item1.getId());
        TypedQuery<Item> query = em.createQuery("Select i from Item i", Item.class);
        List<Item> items = query.getResultList();

        assertThat(items, hasSize(1));
        assertThat(items, hasItem(item2));
    }

    @Test
    void testAddComment() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(null, "item", "itemDescription", true, user1, null);
        itemRepository.save(item);
        Booking booking = new Booking(null, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(10),
                item, user2, Status.APPROVED);
        bookingRepository.save(booking);
        Comment comment = new Comment(null, "comment to item", item, null, null);
        itemService.addComment(user2.getId(), item.getId(), comment);
        TypedQuery<Comment> query = em.createQuery("Select c from Comment c where c.id = :id", Comment.class);
        Comment addedComment = query.setParameter("id", comment.getId()).getSingleResult();

        assertThat(addedComment.getId(), equalTo(comment.getId()));
        assertThat(addedComment.getText(), equalTo("comment to item"));
        assertThat(addedComment.getAuthor(), equalTo(user2));
        assertThat(addedComment.getCreated(), notNullValue());
    }

    @Test
    void testAddCommentBookingTimeNotFinishedReturnsException() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(null, "item", "itemDescription", true, user1, null);
        itemRepository.save(item);
        Booking booking = new Booking(null, LocalDateTime.now().minusDays(20), LocalDateTime.now().plusDays(10),
                item, user2, Status.APPROVED);
        bookingRepository.save(booking);
        Comment comment = new Comment(null, "comment to item", item, null, null);

        Assertions.assertThrows( NotAuthorisedRequestException.class,
                () -> itemService.addComment(user2.getId(), item.getId(), comment));
    }

    @Test
    void testAddCommentByOwnerReturnsException() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item = new Item(null, "item", "itemDescription", true, user1, null);
        itemRepository.save(item);
        Booking booking = new Booking(null, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(10),
                item, user2, Status.APPROVED);
        bookingRepository.save(booking);
        Comment comment = new Comment(null, "comment to item", item, null, null);

        Assertions.assertThrows( NotAuthorisedRequestException.class,
                () -> itemService.addComment(user1.getId(), item.getId(), comment));
    }

    @Test
    void testFindByItemIdWithBookings() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        Item item1 = new Item(null, "item1", "item1Description", true, user1, null);
        Item item2 = new Item(null, "item2", "item2Description", true, user1, null);
        itemRepository.save(item1);
        itemRepository.save(item2);
        Booking booking1 = new Booking(null, LocalDateTime.now().minusDays(20), LocalDateTime.now().minusDays(10),
                item1, user2, Status.APPROVED);
        Booking booking2 = new Booking(null, LocalDateTime.now().minusDays(5), LocalDateTime.now().minusDays(1),
                item1, user2, Status.APPROVED);
        Booking booking3 = new Booking(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(5),
                item1, user2, Status.APPROVED);
        Booking booking4 = new Booking(null, LocalDateTime.now().plusDays(7), LocalDateTime.now().plusDays(10),
                item1, user2, Status.APPROVED);
        bookingRepository.save(booking1);
        bookingRepository.save(booking2);
        bookingRepository.save(booking3);
        bookingRepository.save(booking4);
        ItemDtoWithBookings itemDtoWithBookings = itemService.findByItemId(user1.getId(), item1.getId());

        assertThat(itemDtoWithBookings.getLastBooking().getId(), equalTo(booking2.getId()));
        assertThat(itemDtoWithBookings.getNextBooking().getId(), equalTo(booking3.getId()));
    }
}
