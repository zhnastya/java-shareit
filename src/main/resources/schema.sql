DROP TABLE IF EXISTS users cascade;
DROP TABLE IF EXISTS items cascade;
DROP TABLE IF EXISTS bookings cascade;
DROP TABLE IF EXISTS comments cascade;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(100) UNIQUE,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100),
    description  VARCHAR(250),
    available BOOLEAN,
    owner_id BIGINT,
    FOREIGN KEY(owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    BIGINT,
    booker_id  BIGINT,
    status     VARCHAR(15),
    time_of_created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(booker_id) REFERENCES users(id)
);
CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text      VARCHAR(250),
    item_id   BIGINT,
    author_id BIGINT,
    time_of_created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(item_id) REFERENCES items(id),
    FOREIGN KEY(author_id) REFERENCES users(id)
);