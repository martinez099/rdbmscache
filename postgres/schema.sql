DROP TABLE IF EXISTS books, authors, pictures;

CREATE TABLE IF NOT EXISTS authors (
    id serial PRIMARY KEY,
    name VARCHAR(25)
);

CREATE TABLE IF NOT EXISTS books (
    id serial PRIMARY KEY,
    author_id INT references authors(id),
    title VARCHAR(100)
);

CREATE TABLE IF NOT EXISTS pictures (
    id serial PRIMARY KEY,
    author_id INT references authors(id),
    data bytea
);
