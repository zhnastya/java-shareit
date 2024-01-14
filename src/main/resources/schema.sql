DROP TABLE IF EXISTS requests, comments, bookings, items, users;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name  VARCHAR(250) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);
CREATE TABLE IF NOT EXISTS requests
(
    id        BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    description      VARCHAR(250),
    owner_id   BIGINT,
    created TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY(owner_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name         VARCHAR(100),
    description  VARCHAR(250),
    available BOOLEAN NOT NULL,
    owner_id BIGINT NOT NULL,
    request_id BIGINT,
    FOREIGN KEY(owner_id) REFERENCES users(id),
    FOREIGN KEY (request_id) REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id    BIGINT NOT NULL,
    booker_id  BIGINT NOT NULL,
    status     VARCHAR(15) NOT NULL,
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