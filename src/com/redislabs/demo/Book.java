package com.redislabs.demo;

public class Book {

    private int id;

    private int authorId;

    private String name;

    public Book(int id, int authorId, String name) {
        this.id = id;
        this.authorId = authorId;
        this.name = name;
    }

    public int getId() {
        return this.id;
    }

    public int getAuthorId() {
        return this.authorId;
    }

    public String getName() {
        return this.name;
    }
}