create table IF NOT EXISTS GENRES
(
    GENRE_ID INTEGER auto_increment not null,
    GENRE    CHARACTER VARYING(50) not null,
    constraint GENRE_PK
        primary key (GENRE_ID)
);

create table IF NOT EXISTS MPA
(
    MPA_ID INTEGER auto_increment not null,
    MPA    CHARACTER VARYING(50) not null,
    constraint MPA_PK
        primary key (MPA_ID)
);


create table if not exists FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING(100) not null,
    DESCRIPTION  CHARACTER VARYING(255) not null,
    RELEASE_DATE DATE                   not null,
    DURATION     INTEGER                not null,
    MPA_ID       INTEGER,
    constraint FILMS_PK
        primary key (FILM_ID),
    constraint FILMS___MPA___PK
        foreign key (MPA_ID) references MPA
);

create table if not exists FILM_GENRE
(
    FILMS_ID INTEGER not null,
    GENRE_ID INTEGER not null,
    constraint FILM_GENRE__FILMS__FK
        foreign key (FILMS_ID) references FILMS,
    constraint FILM_GENRE__GENRES_FK
        foreign key (GENRE_ID) references GENRES
);

create table if not exists USERS
(
    USER_ID  BIGINT auto_increment,
    EMAIL    CHARACTER VARYING(255) not null,
    LOGIN    CHARACTER VARYING(255) not null,
    NAME     CHARACTER VARYING(255),
    BIRTHDAY DATE,
    constraint USERS_PK
    primary key (USER_ID)
);

create table if not exists FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint FRIENDS___FK
        foreign key (FRIEND_ID) references USERS,
    constraint USERS__FK
        foreign key (USER_ID) references USERS
);

create table if not exists LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKES__FILMS__FK
    foreign key (FILM_ID) references FILMS,
    constraint LIKES___USERS__FK
    foreign key (USER_ID) references USERS
);









