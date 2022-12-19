package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.persistence.TypedQuery;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@DataJpaTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {

    private final TestEntityManager em;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void search() {
        User user = userRepository.save(new User(null, "user", "user@email.ru"));
        Item item1 = itemRepository.save(new Item(
                null,
                "item",
                "description",
                true,
                user,
                null
        ));
        Item item2 = itemRepository.save(new Item(
                null,
                "item to search",
                "description",
                true,
                user,
                null
        ));
        Item item3 = itemRepository.save(new Item(
                null,
                "item",
                "Search description",
                true,
                user,
                null
        ));

        TypedQuery<Item> query = em.getEntityManager()
                .createQuery("Select i from Item i", Item.class);
        List<Item> items = query.getResultList();
        assertThat(items, hasSize(3));

        List<Item> itemsFound = itemRepository.search("search", Pageable.ofSize(10));
        assertThat(itemsFound, hasSize(2));
        assertThat(itemsFound.get(0).getId(), is(item2.getId()));
        assertThat(itemsFound.get(1).getId(), is(item3.getId()));
    }
}