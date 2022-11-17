DROP TABLE IF EXISTS
    users,
    requests,
    items,
    bookings,
    comments;

CREATE TABLE IF NOT EXISTS users (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
     name VARCHAR(255) NOT NULL,
     email VARCHAR(512) NOT NULL,
     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description VARCHAR(100),
    requestor_id BIGINT REFERENCES users(id),
    created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS items (
     id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
     name VARCHAR(255) NOT NULL,
     description VARCHAR(1024) NOT NULL,
     is_available BOOLEAN NOT NULL,
     owner_id BIGINT REFERENCES users(id),
     request_id BIGINT REFERENCES requests(id)
);

CREATE TABLE IF NOT EXISTS bookings (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items(id),
    booker_id BIGINT REFERENCES users(id),
    status VARCHAR(20)
);

CREATE TABLE IF NOT EXISTS comments (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(500) NOT NULL,
    item_id BIGINT REFERENCES items(id),
    author_id BIGINT REFERENCES users(id),
    created TIMESTAMP WITHOUT TIME ZONE
);