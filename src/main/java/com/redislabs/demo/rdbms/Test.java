package com.redislabs.demo.rdbms;

import com.redislabs.demo.rdbms.application.API;
import com.redislabs.demo.rdbms.infrastructure.pojo.Author;
import com.redislabs.demo.rdbms.infrastructure.pojo.Book;
import com.redislabs.demo.rdbms.infrastructure.pojo.Picture;


public class Test {

    API api = new API();

    public static void main(String[] args) {

        Test t = new Test();

        t.testAuthor();
        t.testBook();
        t.testPicture();

        t.close();
    }

    void testAuthor() {

        Author a = api.get(Author.class, 1);
        assert a.getId() == 1;

        a.setName("anotherName");
        api.set(a);
        a = api.get(Author.class, 1);
        assert a.getName().equals("anotherName");

        a = new Author(6, "newAuthor");
        api.set(a);
        a = api.get(Author.class, 6);
        assert a.getName().equals("newAuthor");

        api.del(Author.class, 6);
        a = api.get(Author.class, 6);
        assert a == null;
    }

    void testBook() {

        Book b = api.get(Book.class, 1);
        assert b.getId() == 1;

        b.setTitle("anotherName");
        api.set(b);
        b = api.get(Book.class, 1);
        assert b.getTitle().equals("anotherName");

        b = new Book(9, 1, "newBook");
        api.set(b);
        b = api.get(Book.class, 9);
        assert b.getTitle().equals("newBook");

        api.del(Book.class, 9);
        b = api.get(Book.class, 9);
        assert b == null;
    }

    void testPicture() {

        Picture p = api.get(Picture.class, 1);
        assert p.getId() == 1;

        p.setAuthorId(6);
        api.set(p);
        p = api.get(Picture.class, 1);
        assert p.getAuthorId() == 6;

        p = new Picture(6, 1, "ASdfadfasdfasdf".getBytes() );
        api.set(p);
        p = api.get(Picture.class, 6);
        assert p.getAuthorId() == 1;

        api.del(Picture.class, 6);
        p = api.get(Picture.class, 6);
        assert p == null;
    }

    void close() {
        this.api.close();
    }
}
