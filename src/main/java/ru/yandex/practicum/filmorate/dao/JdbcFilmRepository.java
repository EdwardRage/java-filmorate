package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@RequiredArgsConstructor
@Repository
public class JdbcFilmRepository implements FilmRepository/*, MpaRepository*/ {

    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Collection<Film> get() {
        String sql = "SELECT * FROM FILMS AS f "
                + "LEFT JOIN MPA AS m ON m.MPA_ID = f.MPA_ID";

        return jdbc.query(sql, this::makeFilm);
    }

    public List<Film> getAll() {
        // получить все жанры
        // получить все фильмы с рейтингом
        // получить связи жанры -фильмы static record GenreRelation(film_id, genre_id)
        // объединить эти 3 запроса
        String sql = "SELECT * FROM FILMS AS f LEFT JOIN MPA AS m ON m.MPA_ID = f.MPA_ID";

        return jdbc.getJdbcOperations().query(sql, this::makeFilm);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        // select from films join MPA join Genres where film_id = :id
        // ResultSetExtractor
        // while
        /*ResultSet rs;
        Film film = null;
        while (rs.next()) {
            if (film == null) {
                film = new Film();
            }
            // добавляем жанры
        }*/
        // возвращаем фильм return film
        /*String sql = "SELECT * FROM FILMS AS f " +
                     "LEFT JOIN MPA AS m ON m.MPA_ID = f.MPA_ID " +
                     "WHERE f.FILM_ID = :filmsId";*/
        String sql = "SELECT f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, G2.GENRE, m.MPA  FROM FILMS as f" +
        "left join FILM_GENRE FG on f.FILM_ID = FG.FILMS_ID" +
        "left join GENRES G2 on G2.GENRE_ID = FG.GENRE_ID" +
        "left join MPA M on M.MPA_ID = f.MPA_ID" +
        "WHERE FILM_ID = :filmId";
        Map<String, Object> params = new HashMap<>();
        params.put("filmsId", filmId);
        return Optional.ofNullable(jdbc.queryForObject(sql, params, this::makeFilm));
        //return Optional.empty();
    }

    @Override
    public Film create(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        /*String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, " +
                "RELEASE_DATE, DURATION, MPA_ID)" +
                " VALUES (:NAME, :DESCRIPTION, :RELEASE_DATE, :DURATION, :MPA_ID";*/

        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID) " +
        "VALUES ( :NAME, :DESCRIPTION, :RELEASE_DATE, :DURATION, :MPA_ID ) RETURNING FILM_ID";

        /*"INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA_ID)
        VALUES ( :NAME, :DESCRIPTION, :RELEASE_DATE, :DURATION, :MPA_ID )";*/
        Map<String, Object> params = new HashMap<>();
        params.put("NAME", film.getName());
        params.put("DESCRIPTION", film.getDescription());
        params.put("RELEASE_DATE", film.getReleaseDate());
        params.put("DURATION", film.getDuration());
        params.put("MPA_ID", film.getMpa().getMpaId());
        //params.put("genre", film.getGenre());

        /*MapSqlParameterSource params = new MapSqlParameterSource()
                .addValues(Map.of("NAME", film.getName()))
                .addValues(Map.of("DESCRIPTION", film.getDescription()))
                .addValues(Map.of("RELEASE_DATE", film.getReleaseDate()))
                .addValues(Map.of("DURATION", film.getDuration()))
                .addValues(Map.of("MPA_ID", film.getMpa().getMpaId()));*/

        jdbc.update(sql, params);
        // создать связи фильмы-жанры
        // batch в него оборавичаются sql запросы
        //film.setId(keyHolder.getKeyAs(Long.class));
        film.setId(Objects.requireNonNull(keyHolder.getKeyAs(Long.class)));
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET NAME = :NAME, DESCRIPTION = :DESCRIPTION, " +
                "RELEASE_DATE = :RELEASE_DATE, DURATION = :DURATION, MPA_ID = :MPA_ID";
        Map<String, Object> params = new HashMap<>();
        params.put("NAME", film.getName());
        params.put("DESCRIPTION", film.getDescription());
        params.put("RELEASE_DATE", film.getReleaseDate());
        params.put("DURATION", film.getDuration());
        params.put("MPA_ID", film.getMpa().getMpaId());
        //params.put("GENRE", film.getGenre());
        jdbc.update(sql, params);
        return film;
    }

    @Override
    public void addLike(long filmId, long userId) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (:filmId, :userId)";
        jdbc.getJdbcOperations().update(sql, Map.of("filmId", filmId,
                                                    "userId", userId));
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        String sql = "DELETE INTO LIKES (FILM_ID, USER_ID) VALUES (:filmId, :userId)";
        jdbc.getJdbcOperations().update(sql, Map.of("filmId", filmId,
                "userId", userId));
    }

    @Override
    public List<Integer> getLikes(long filmId) {
        String sql = "SELECT COUNT(l.USER_ID) FROM LIKES AS l WHERE l.FILM_ID = :filmId";
        return jdbc.getJdbcOperations().queryForList(sql, Integer.class, Map.of("filmId", filmId));
    }

    @Override
    public List<Film> getTopPopular(int count) {
        String sql = "SELECT * FROM FILMS AS f " +
                "LEFT JOIN LIKES AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY COUNT(l.FILM_ID) " +
                "DESC LIMIT :count";
        return jdbc.query(sql, Map.of("count", count), this::makeFilm);
    }

    private void addGenre(List<Film> films) {

    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getLong("film_if"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(makeMpa(rs, rowNum));
        //film.setGenre(new ArrayList<>());
        //film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_code")));
        //film.setMpa(new Mpa(rs.getInt("MPA_ID"), rs.getString("MPA_CODE")));
        return film;
    }

    private Mpa makeMpa(ResultSet rs, int rowNum) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setMpaId(rs.getLong("mpa_id"));
        mpa.setMpa(rs.getString("mpa_code"));
        return mpa;
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setGenreId(rs.getLong("genre_id"));
        genre.setGenre(rs.getString("genre"));
        return genre;
    }
}
