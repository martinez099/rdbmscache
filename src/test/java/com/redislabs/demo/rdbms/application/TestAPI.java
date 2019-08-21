package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.infrastructure.pojo.Author;
import com.redislabs.demo.rdbms.infrastructure.pojo.Book;
import com.redislabs.demo.rdbms.infrastructure.pojo.Picture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class TestAPI {

    static API api = new API("redis://localhost:6379", "jdbc:postgresql://127.0.0.1:5432/postgres?user=postgres&password=postgres");

    @BeforeAll
    static void insertTestData() {
        api.set(new Author("Jack London"));
        api.set(new Author("Honore de Balzac"));
        api.set(new Author("Lion Feuchtwanger"));
        api.set(new Author("Emile Zola"));
        api.set(new Author("Truman Capote"));

        api.set(new Book(1, "Call of the Wild"));
        api.set(new Book(1, "Martin Eden"));
        api.set(new Book(2, "Old Goriot"));
        api.set(new Book(2, "Cousin Bette"));
        api.set(new Book(3, "Jew Suess"));
        api.set(new Book(4, "Nana"));
        api.set(new Book(4, "The Belly of Paris"));
        api.set(new Book(5, "In Cold blood"));
        api.set(new Book(5, "Breakfast at Tiffany"));

        api.set(new Picture(1, new byte[]{12,12,12,12,12,123}));
        api.set(new Picture(2, new byte[]{15,15,15,15,15,123}));
        api.set(new Picture(3, new byte[]{16,16,16,16,16,123}));
        api.set(new Picture(4, new byte[]{17,17,17,17,17,123}));
        api.set(new Picture(5, new byte[]{11,11,11,11,11,123}));
    }

    @Test
    void testAuthor() {

        Author a = api.get(Author.class, 1);
        assert a.getId() == 1;

        a.setName("anotherName");
        assert api.set(a);

        a = api.get(Author.class, 1);
        assert a.getName().equals("anotherName");

        a = new Author("newAuthor");
        assert api.set(a);

        a = api.get(Author.class, a.getId());
        assert a.getName().equals("newAuthor");

        assert api.del(Author.class, a.getId());
        a = api.get(Author.class, a.getId());
        assert a == null;
    }

    @Test
    void testBook() {

        Book b = api.get(Book.class, 1);
        assert b.getId() == 1;

        b.setTitle("anotherName");
        assert api.set(b);

        b = api.get(Book.class, 1);
        assert b.getTitle().equals("anotherName");

        b = new Book(1, "newBook");
        assert api.set(b);

        b = api.get(Book.class, b.getId());
        assert b.getTitle().equals("newBook");

        assert api.del(Book.class, b.getId());
        b = api.get(Book.class, b.getId());
        assert b == null;
    }

    @Test
    void testPicture() {

        Picture p = api.get(Picture.class, 1);
        assert p.getId() == 1;

        p.setAuthorId(4);
        assert api.set(p);

        p = api.get(Picture.class, 1);
        assert p.getAuthorId() == 4;

        p = new Picture(1, "ASdfadfasdfasdf".getBytes());
        assert api.set(p);

        p = api.get(Picture.class, p.getId());
        assert p.getAuthorId() == 1;

        assert api.del(Picture.class, p.getId());
        p = api.get(Picture.class, p.getId());
        assert p == null;
    }

    @AfterAll
    static void tearDown() {
        api.close();
    }
}
