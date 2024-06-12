package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.UserRepository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validate.ValidateServiceImpl;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository jdbcUser;

    private final ValidateServiceImpl validate;

    @Override
    public Collection<User> get() {
        return jdbcUser.get();
    }

    @Override
    public User getUserById(long userId) {
        return jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
    }

    @Override
    public User create(User user) {
        validate.validateCreate(user);
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        return jdbcUser.create(user);
    }

    public User update(User newUser) {
        User oldUser =jdbcUser.getUserById(newUser.getId())
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
        return jdbcUser.update(newUser);
    }

    @Override
    public void addFriends(long userId, long friendId) {
        User user = jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        User friend = jdbcUser.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendId + " не найден"));

        jdbcUser.addFriend(user.getId(), friend.getId());
        //jdbcUser.addFriend(userId, friendId);

    }

    public void deleteFriend(long userId, long friendId) {
        User user = jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        User friend = jdbcUser.getUserById(friendId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + friendId + " не найден"));

        jdbcUser.deleteFriend(user.getId(), friend.getId());
    }

    @Override
    public List<User> getFriends(long userId) {
        User user = jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        return jdbcUser.getFriends(user.getId());
    }

    public Set<User> getCommonFriends(long userId, long otherId) {
        User user = jdbcUser.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + userId + " не найден"));
        User otherUser = jdbcUser.getUserById(otherId)
                .orElseThrow(() -> new NotFoundException("Пользователь с Id = " + otherId + " не найден"));

        List<User> userSet = jdbcUser.getFriends(user.getId());
        List<User> otherSet = jdbcUser.getFriends(otherUser.getId());

        Set<User> commonFriends = new HashSet<>(userSet);
        commonFriends.retainAll(otherSet);
        return commonFriends;
    }

    private void checkLogin(User newUser) {
        for (User user : jdbcUser.get()) {
            if (user.getId().equals(newUser.getId()) && user.getLogin().equals(newUser.getLogin())) {
                throw new DuplicatedDataException("Логин не может повторяться");
            }
        }
    }

    private void checkEmail(User newUser) {
        for (User user : jdbcUser.get()) {
            if (user.getId().equals(newUser.getId()) && user.getEmail().equals(newUser.getEmail())) {
                throw new DuplicatedDataException("Имейл не может повторяться");
            }
        }
    }
}
