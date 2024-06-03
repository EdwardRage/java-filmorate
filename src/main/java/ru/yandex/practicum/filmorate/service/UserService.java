package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validate.ValidateServiceImpl;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserStorage userStorage;

    private final ValidateServiceImpl validate;

    public Collection<User> getUsers() {
        return userStorage.get();
    }

    public User getUserById(long userId) {
        return userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
    }

    public User create(User user) {
        validate.validateCreate(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User newUser) {
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User oldUser = userStorage.getUserById(newUser.getId())
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + newUser.getId() + " не найден"));
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null) {
            validate.validateUpdate(newUser);
            oldUser.setBirthday(newUser.getBirthday());
        }
        if (newUser.getLogin() != null) {
            validate.validateUpdate(newUser);
            checkLogin(newUser);
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getEmail() != null) {
            validate.validateUpdate(newUser);
            checkEmail(newUser);
            oldUser.setEmail(newUser.getEmail());
        }
        return userStorage.update(oldUser);
    }

    public void addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendId + " не найден"));

        userStorage.addFriend(user, friend);

    }

    public void deleteFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        User friend = userStorage.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendId + " не найден"));

        userStorage.deleteFriend(user, friend);
    }


    public Set<User> getFriends(long userId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        return userStorage.getFriends(user);
    }

    public Set<User> getCommonFriends(long userId, long otherId) {
        User user = userStorage.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        User otherUser = userStorage.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + otherId + " не найден"));

        Set<User> userSet = userStorage.getFriends(user);
        Set<User> otherSet = userStorage.getFriends(otherUser);

        Set<User> commonFriends = new HashSet<>(userSet);
        commonFriends.retainAll(otherSet);
        return commonFriends;
    }

    private void checkLogin(User newUser) {
        for (User user : userStorage.get()) {
            if (user.getId().equals(newUser.getId()) && user.getLogin().equals(newUser.getLogin())) {
                throw new DuplicatedDataException("Логин не может повторяться");
            }
        }
    }

    private void checkEmail(User newUser) {
        for (User user : userStorage.get()) {
            if (user.getId().equals(newUser.getId()) && user.getEmail().equals(newUser.getEmail())) {
                throw new DuplicatedDataException("Имейл не может повторяться");
            }
        }
    }
}
