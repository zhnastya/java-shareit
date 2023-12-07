DROP TABLE if exists users;
DROP TABLE if exists items;
DROP TABLE if exists bookings;
DROP TABLE if exists comments;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100),
    description  VARCHAR(250),
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    FOREIGN KEY(owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT NOT NULL,
    booker_id  BIGINT NOT NULL,
    status     VARCHAR(15) NOT NULL,
    time_of_created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(booker_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      VARCHAR(250),
    item_id   BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    time_of_created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(author_id) REFERENCES users(id)
);