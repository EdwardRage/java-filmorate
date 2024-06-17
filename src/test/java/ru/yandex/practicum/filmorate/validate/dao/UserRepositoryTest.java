package ru.yandex.practicum.filmorate.validate.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserRepositoryTest {
    private final UserRepository userRepository;
    User user1 = new User();
    User user2 = new User();
    User user3 = new User();

    @BeforeEach
    void createUsersForTest() {
        user1.setName("user1");
        user1.setEmail("user1@mail.ru");
        user1.setLogin("user1login");
        user1.setBirthday(LocalDate.of(2000, 12,12));

        user2.setName("user2");
        user2.setEmail("user2@mail.ru");
        user2.setLogin("user2login");
        user2.setBirthday(LocalDate.of(2000, 12,12));

        user3.setName("user3");
        user3.setEmail("user3@mail.ru");
        user3.setLogin("user3login");
        user3.setBirthday(LocalDate.of(2000, 12,12));

        userRepository.create(user1);
        userRepository.create(user2);
        userRepository.create(user3);
    }

    @Test
    void getUserById() {
        User userTest = userRepository.getUserById(1).orElseThrow();

        assertEquals(user1.getName(), userTest.getName());
    }

    @Test
    void addFriendsTest() {
        userRepository.addFriend(user1.getId(), user2.getId());

        List<User> getFriends1 = userRepository.getFriends(user1.getId());
        List<User> getFriends2 = userRepository.getFriends(user2.getId());

        assertEquals(1, getFriends1.size());
        assertEquals(0, getFriends2.size());
    }

    @Test
    void deleteFriendsTest() {
        userRepository.addFriend(user1.getId(), user2.getId());
        userRepository.addFriend(user2.getId(), user1.getId());

        userRepository.deleteFriend(user1.getId(), user2.getId());
        List<User> getFriends1 = userRepository.getFriends(user1.getId());
        List<User> getFriends2 = userRepository.getFriends(user2.getId());


        assertEquals(0, getFriends1.size());
        assertEquals(1, getFriends2.size());
    }
}
