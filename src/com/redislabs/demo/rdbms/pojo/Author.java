package com.redislabs.demo.rdbms.pojo;

public class Author extends Base {

    private String name;

    public Author(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
