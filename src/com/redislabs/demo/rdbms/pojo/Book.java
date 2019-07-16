package com.redislabs.demo.rdbms.pojo;

public class Book extends Base {

    private int authorId;

    private String name;

    public Book(int id, int authorId, String name) {
        super(id);
        this.authorId = authorId;
        this.name = name;
    }

    public int getAuthorId() {
        return this.authorId;
    }

    public void setAuthorId(int id) {
        this.authorId = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
