package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class JdbcUserRepository implements UserRepository {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Collection<User> get() {
        String sql = "SELECT * FROM USERS";
        //return jdbc.getJdbcOperations().query(sql, this::makeUser);
        return jdbc.query(sql, this::makeUser);
    }

    @Override
    public Optional<User> getUserById(long userId) {

        String sql = "SELECT * FROM USERS WHERE USER_ID = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        User user = jdbc.queryForObject(sql, params, this::makeUser);
        //User user1 = jdbc.getJdbcOperations().queryForObject(sql, params, this::makeUser);
        return Optional.ofNullable(user);
    }

    /*@Override
    public User getUserById(long userId) {
        String sql = "SELECT * FROM users WHERE user_id = :userId";
        return jdbc.queryForObject(sql, Map.of("userId", userId), this::makeUser);
    }*/

    @Override
    public User create(User user) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES ( :EMAIL, :LOGIN, :NAME,  :BIRTHDAY)";
        Map<String, Object> params = new HashMap<>();
        params.put("EMAIL", user.getEmail());
        params.put("LOGIN", user.getLogin());
        params.put("NAME", user.getName());
        params.put("BIRTHDAY", user.getBirthday());

        jdbc.update(sql, params);
        user.setId(keyHolder.getKeyAs(Long.class));
        return user;
    }

    @Override
    public User update(User user) {

        String sql = "UPDATE USERS SET EMAIL = :EMAIL, LOGIN = :LOGIN, NAME = :NAME, BIRTHDAY = :BIRTHDAY " +
        "WHERE USER_ID = :userId";
        Map<String, Object> params = new HashMap<>();
        params.put("userId", user.getId());
        params.put("EMAIL", user.getEmail());
        params.put("LOGIN", user.getLogin());
        params.put("NAME", user.getName());
        params.put("BIRTHDAY", user.getBirthday());
        /*int rowCount = */
        jdbc.update(sql, params);
        /*if (rowCount != 1) {
            throw new NotFoundException("Пользователь не найден");
        }*/
        return user;
    }

    @Override
    public void addFriend(long userId, long friendId) {
        String sql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES ( :userId, :friendId)";
        jdbc.update(sql, Map.of("userId", userId,"friendId", friendId));
    }

    @Override
    public void deleteFriend(long userId, long friendId) {
        String sql = "DELETE INTO FRIENDS (USER_ID, FRIEND_ID) VALUES ( :userId, :friendId)";
        jdbc.update(sql, Map.of("userId", userId, "friendId", friendId));
    }

    public List<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM FRIENDS AS f JOIN USERS AS u ON f.FRIEND_ID = u.USER_ID WHERE f.USER_ID = :userId";

        return jdbc.query(sql, Map.of("userId", userId), this::makeUser);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {

        User user = new User();
        user.setId(rs.getLong("user_id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());

        return user;
    }
}
