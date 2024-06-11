package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.service.UserServiceImpl;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userServiceImpl;

    @GetMapping
    public Collection<User> getUsers() {
        return userServiceImpl.get();
    }

    @GetMapping("/{userId}")
    public User getUserById(@PathVariable long userId) {
        return userServiceImpl.getUserById(userId);
    }

    @PostMapping
    public User create(@RequestBody User user) {
        return userServiceImpl.create(user);
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        return userServiceImpl.update(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        userServiceImpl.addFriends(id, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable long userId, @PathVariable long friendId) {
        userServiceImpl.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable long userId) {
        return userServiceImpl.getFriends(userId);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable long userId, @PathVariable long otherId) {
        return userServiceImpl.getCommonFriends(userId, otherId);
    }
}
