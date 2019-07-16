package com.redislabs.demo.rdbms;

import com.redislabs.demo.rdbms.application.API;
import com.redislabs.demo.rdbms.pojo.Author;


public class Test {

    public static void main(String[] args) {

        API api = new API();

        Author a = api.getAuthor(1);

        assert a.getId() == 1;

        a.setName("anotherName");

        api.setAuthor(a);

        a = api.getAuthor(1);

        assert a.getName().equals("anotherName");

        a = new Author(6, "newAuthor");

        api.setAuthor(a);

        a = api.getAuthor(6);

        assert a.getName().equals("newAuthor");

        api.delAuthor(6);

        a = api.getAuthor(6);

        assert a == null;

        api.close();

    }
}
