package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Collection<User> get();

    Optional<User> getUserById(long userId);

    User create(User user);

    User update(User newUser); // не используется, удалить

    void addFriend(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    //Set<User> getFriends(User user);
}
