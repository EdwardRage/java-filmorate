package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface UserService {

    User create(User user);

    User update(User user);

    Collection<User> get();

    User getUserById(long userId);

    void addFriends(long userId, long friendId);

    void deleteFriend(long userId, long friendId);

    List<User> getFriends(long userId);

    Set<User> getCommonFriends(long userId, long friendId);
}
