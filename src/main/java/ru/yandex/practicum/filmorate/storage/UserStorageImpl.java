package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class UserStorageImpl implements UserStorage {
    private final Map<Long, User> usersMap = new HashMap<>();
    private final Map<Long, Set<Long>> friendsMap = new HashMap<>();
    private long id = 0;

    @Override
    public Collection<User> get() { // get или getUsers
        return usersMap.values();
    }

    @Override
    public Optional<User> getUserById(long userId) {
        return Optional.ofNullable(usersMap.get(userId));
    }

    @Override
    public User create(User user) {
        user.setId(generateId());
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        usersMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void addFriend(User user, User friend) {
        Set<Long> userFriendsId = friendsMap.computeIfAbsent(user.getId(), id -> new HashSet<>());

        Set<Long> friendFriendsId = friendsMap.computeIfAbsent(friend.getId(), id -> new HashSet<>());

        userFriendsId.add(friend.getId());
        friendFriendsId.add(user.getId());
    }

    @Override
    public void deleteFriend(User user, User friend) {
        Set<Long> userFriendsId = friendsMap.computeIfAbsent(user.getId(), id -> new HashSet<>());
        Set<Long> friendFriendsId = friendsMap.computeIfAbsent(friend.getId(), id -> new HashSet<>());

        userFriendsId.remove(friend.getId());
        friendFriendsId.remove(user.getId());
    }

    @Override
    public Set<User> getFriends(User user) {
        Set<Long> friendsId = friendsMap.computeIfAbsent(user.getId(), id -> new HashSet<>());
        Set<User> friends =  new HashSet<>();
        for (Long id : friendsId) {
            User friend = getUserById(id)
                    .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + id + " не найден"));
            friends.add(friend);
        }
        return friends;
    }

    private long generateId() {
        return ++id;
    }
}
