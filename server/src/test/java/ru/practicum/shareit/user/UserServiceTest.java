package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    @Mock
    private UserRepository mockUserRepository;
    private final UserRepository userRepository;
    private final EntityManager em;
    private final UserService userService;

    @Test
    void testFindByIdReturnsException() {
        Mockito
                .when(mockUserRepository.findById(1L))
                .thenReturn(Optional.empty());
        final UserNotFoundException exception = Assertions.assertThrows(
                UserNotFoundException.class,
                () -> userService.findById(1L));
        Assertions.assertEquals("Пользователь не найден", exception.getMessage());
    }

   @Test
    void testCreateUser() {
        User user = new User(null, "user", "user@email.ru");
        userService.create(user);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User userSaved = query.setParameter("email", user.getEmail()).getSingleResult();

        assertThat(userSaved.getId(), notNullValue());
        assertThat(userSaved.getName(), equalTo(user.getName()));
        assertThat(userSaved.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void testUpdateUserName() {
        User user = new User(null, "user","user@email.ru");
        User savedUser = userRepository.save(user);
        User userWithUpdatedName = new User(savedUser.getId(), "updatedUser", null);
        userService.update(userWithUpdatedName);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userUpdated = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(userUpdated.getId(), equalTo(savedUser.getId()));
        assertThat(userUpdated.getName(), equalTo("updatedUser"));
        assertThat(userUpdated.getEmail(), equalTo("user@email.ru"));
    }

    @Test
    void testUpdateUserEmail() {
        User user = new User(null, "user","user@email.ru");
        User savedUser = userRepository.save(user);
        User userWithUpdatedEmail = new User(savedUser.getId(), null, "updatedUser@email.ru");
        userService.update(userWithUpdatedEmail);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userUpdated = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(userUpdated.getId(), equalTo(savedUser.getId()));
        assertThat(userUpdated.getName(), equalTo("user"));
        assertThat(userUpdated.getEmail(), equalTo("updatedUser@email.ru"));
    }

    @Test
    void testUpdateUserWithBlankName() {
        User user = new User(null, "user","user@email.ru");
        User savedUser = userRepository.save(user);
        User userWithBlankName = new User(savedUser.getId(), " ", null);
        userService.update(userWithBlankName);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userUpdated = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(userUpdated.getId(), equalTo(savedUser.getId()));
        assertThat(userUpdated.getName(), equalTo("user"));
        assertThat(userUpdated.getEmail(), equalTo("user@email.ru"));
    }

    @Test
    void testUpdateUserNameAndEmail() {
        User user = new User(null, "user","user@email.ru");
        User savedUser = userRepository.save(user);
        User updatedUser = new User(savedUser.getId(), "updatedUser", "updatedUser@email.ru");
        userService.update(updatedUser);
        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);
        User userUpdated = query.setParameter("id", user.getId()).getSingleResult();

        assertThat(userUpdated.getId(), equalTo(savedUser.getId()));
        assertThat(userUpdated.getName(), equalTo("updatedUser"));
        assertThat(userUpdated.getEmail(), equalTo("updatedUser@email.ru"));
    }

    @Test
    void testFindAll() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        User user3 = new User(null, "user3", "user3@email.ru");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        List<User> users = userService.findAll();

        assertThat(users, allOf(hasItem(savedUser1), hasItem(savedUser2), hasItem(savedUser3)));
    }

    @Test
    void testFindById() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        User user3 = new User(null, "user3", "user3@email.ru");
        userRepository.save(user1);
        userRepository.save(user2);
        userRepository.save(user3);
        User user = userService.findById(user2.getId());

        assertThat(user, equalTo(user2));
    }

    @Test
    void testDelete() {
        User user1 = new User(null, "user1", "user1@email.ru");
        User user2 = new User(null, "user2", "user2@email.ru");
        User user3 = new User(null, "user3", "user3@email.ru");
        User savedUser1 = userRepository.save(user1);
        User savedUser2 = userRepository.save(user2);
        User savedUser3 = userRepository.save(user3);
        userService.delete(savedUser2.getId());
        TypedQuery<User> query = em.createQuery("Select u from User u", User.class);
        List<User> users = query.getResultList();

        assertThat(users, allOf(hasItem(savedUser1), not(hasItem(savedUser2)), hasItem(savedUser3)));
        assertThat(users, hasSize(2));
    }
}
