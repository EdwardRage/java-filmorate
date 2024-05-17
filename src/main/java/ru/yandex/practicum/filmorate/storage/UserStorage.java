package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Collection<User> get();

    Optional<User> getUserById(long userId);

    User create(User user);

    User update(User newUser);

    void addFriend(User user, User friend);

    void deleteFriend(User user, User friend);

    Set<User> getFriends(User user);
}
