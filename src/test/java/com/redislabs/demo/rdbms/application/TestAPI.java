package com.redislabs.demo.rdbms.application;

import com.redislabs.demo.rdbms.domain.Author;
import com.redislabs.demo.rdbms.domain.Book;
import com.redislabs.demo.rdbms.domain.Picture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;


public class TestAPI {

    static API api = new API("redis://localhost:6379",
            "jdbc:postgresql://127.0.0.1:5432/postgres?user=postgres&password=postgres");

    @BeforeAll
    static void insertTestData() {
        Author a1 = new Author("Jack London");
        api.set(a1);

        Author a2 = new Author("Honore de Balzac");
        api.set(a2);

        Author a3 = new Author("Lion Feuchtwanger");
        api.set(a3);

        Author a4 = new Author("Emile Zola");
        api.set(a4);

        Author a5 = new Author("Truman Capote");
        api.set(a5);

        api.set(new Book(a1, "Call of the Wild"));
        api.set(new Book(a1, "Martin Eden"));
        api.set(new Book(a2, "Old Goriot"));
        api.set(new Book(a2, "Cousin Bette"));
        api.set(new Book(a3, "Jew Suess"));
        api.set(new Book(a4, "Nana"));
        api.set(new Book(a4, "The Belly of Paris"));
        api.set(new Book(a5, "In Cold blood"));
        api.set(new Book(a5, "Breakfast at Tiffany"));

        api.set(new Picture(a1, new byte[]{12,12,12,12,12,123}));
        api.set(new Picture(a2, new byte[]{15,15,15,15,15,123}));
        api.set(new Picture(a3, new byte[]{16,16,16,16,16,123}));
        api.set(new Picture(a4, new byte[]{17,17,17,17,17,123}));
        api.set(new Picture(a5, new byte[]{11,11,11,11,11,123}));
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

        assert api.del(a);
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

        Author a1 = api.get(Author.class, 1);
        b = new Book(a1, "newBook");
        assert api.set(b);

        b = api.get(Book.class, b.getId());
        assert b.getTitle().equals("newBook");

        assert api.del(b);
        b = api.get(Book.class, b.getId());
        assert b == null;
    }

    @Test
    void testPicture() {

        Picture p = api.get(Picture.class, 1);
        assert p.getId() == 1;

        Author a4 = api.get(Author.class, 4);
        p.setAuthor(a4);
        assert api.set(p);

        p = api.get(Picture.class, 1);
        assert p.getAuthor().equals(a4);

        Author a1 = api.get(Author.class, 1);
        byte[] newData = "ASdfadfasdfasdf".getBytes();
        p = new Picture(a1, newData);
        assert api.set(p);
        assert Arrays.equals(p.getData(), newData);

        p = api.get(Picture.class, p.getId());
        assert p.getAuthor().equals(a1);

        assert api.del(p);
        p = api.get(Picture.class, p.getId());
        assert p == null;
    }

    @AfterAll
    static void tearDown() {
        api.reset();
        api.close();
    }
}
