package com.redislabs.demo.rdbms;

import com.redislabs.demo.rdbms.application.API;
import com.redislabs.demo.rdbms.pojo.Author;


public class Test {

    public static void main(String[] args) {

        API api = new API();

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

        api.close();

    }
}
